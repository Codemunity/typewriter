import java.io.File
import java.util.concurrent.TimeUnit

import akka.actor.ActorSystem
import akka.http.scaladsl.Http.ServerBinding
import files.FileCrawler.{Result, Success}
import files.assets.{ImageUtils, JavascriptCompiler, SassCompiler}
import files.handlers.PageTemplateHandler
import files.{FileCrawler, FileIO}
import models.{Config, Post}
import parsers.ConfigParser
import server.WebServer
import stores.PostStore
import stores.PostStore._
import akka.pattern.ask
import akka.util.Timeout

import scala.concurrent.duration._
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


class Typewriter(val workingDirectory: String) {

  val actorSystem = ActorSystem("typewriter")
  val postStore = actorSystem.actorOf(PostStore.props, "PostStore")

  val buildDirPath = s"$workingDirectory/${Config.buildDirName}"

  def loadConfig(implicit ec: ExecutionContext): Future[Config] = {
    val configPath = s"$workingDirectory/${Config.filename}"
    for {
      contents <- FileIO.read(configPath)
    } yield ConfigParser.yamlToModel(contents)
  }

  def clean(implicit ec: ExecutionContext): Future[Unit] = {
    FileIO.deleteRecursively(buildDirPath)
  }

  def build(implicit ec: ExecutionContext): Future[Unit] = {
    implicit val timeout = Timeout(5, TimeUnit.SECONDS)

//    def compile(config: Config): Future[Unit] = {
//      val assetsResult = assets(config)
//      val crawlerResult = new FileCrawler(workingDirectory, config, postStore).crawl(workingDirectory, buildDirPath)
//      val templatesResult = crawlerResult.flatMap { _ =>
//        println("TEMPLATES RESULT")
//        (postStore ? AllOrderedByDate).map {
//          case PostsResult(posts) => evaluateDependentTemplates(config, posts.toList)
//        }
//      }
//
//      assetsResult.onComplete(a => println(s"A COMPLETE: $a"))
//      templatesResult.onComplete(a => println(s"T COMPLETE: $a"))
//
////      assetsResult.map(_ => templatesResult)
//
//      // TODO: Dig deeper and find out why this does not complete
//      Await.result(assetsResult.flatMap(_ => templatesResult), Duration.Inf)
//      Future.successful(Success)
//    }

    for {
      config <- loadConfig
      _ <- FileIO.mkdir(buildDirPath)
      _ <- new FileCrawler(workingDirectory, config, postStore).crawl(workingDirectory, buildDirPath)
      _ <- assets(config)
      PostsResult(posts) <- postStore ? AllOrderedByDate
      res <- evaluateDependentTemplates(config, posts.toList)
    } yield res
  }

  def evaluateDependentTemplates(config: Config, posts: List[Post])(implicit ec: ExecutionContext): Future[Unit] = {

    val mappedPosts = posts.map(_.toMap)

    print(s"POSTS: ${posts.map(_.copy(content = Some(""))).map(_.toMap)}")

    val context: Map[String, Map[String, Object]] = Map(
      "context" ->  Map(
        "allPosts" -> mappedPosts,
        // TODO: Refactor the 3 into the config file
        "latestPosts" -> mappedPosts.take(3)
      )
    )

    val fs = config.postListDependentTemplates
      .map(t => {
        val filename = FileIO.fileNameWithoutExtension(t)
        PageTemplateHandler.handleFile(workingDirectory, s"$workingDirectory/$filename", buildDirPath, context)
      })

    Future.reduce(fs)( (a,b) => a )
  }

  def assets(config: Config)(implicit ec: ExecutionContext): Future[Unit] = {
    assert(new File(buildDirPath).exists())

    val sass: Future[Result] = SassCompiler.compile(workingDirectory)

    val jsFiles = config.jsFiles.map(file => s"$workingDirectory/$file")
    val jsPath = s"$buildDirPath/${config.jsOutputFile}"
    val js: Future[Result] = JavascriptCompiler.compile(jsFiles, jsPath)
//    val img:  Future[Result] =
//      Future.sequence(imgs.map((paths) => ImageUtils.compress(paths._1, paths._2))).map(_.reduce(FileCrawler.reduce))

    Future.sequence(List(sass, js)).map(_ => println("Assets Complete"))
  }

  def make(implicit ec: ExecutionContext): Future[Unit] = clean.flatMap(_ => build)

  def server(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    val server = new WebServer(buildDirPath, port = port)
    server.start
  }

  def run(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = make.flatMap(_ => server(port))

}