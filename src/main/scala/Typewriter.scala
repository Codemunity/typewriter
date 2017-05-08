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
import models.builds.{BuildType, DevelopmentBuild}
import templaters.TagTemplater

import scala.concurrent.{ExecutionContext, Future}


class Typewriter(val workingDirectory: String) {

  val actorSystem = ActorSystem("typewriter")
  val postStore = actorSystem.actorOf(PostStore.props, "PostStore")

  val buildDirPath = s"$workingDirectory/${Config.buildDirName}"

  val tagTemplater = new TagTemplater(workingDirectory)

  def loadConfig(implicit ec: ExecutionContext): Future[Config] = {
    val configPath = s"$workingDirectory/${Config.filename}"
    for {
      contents <- FileIO.read(configPath)
    } yield ConfigParser.jsonToModel(contents)
  }

  def clean(implicit ec: ExecutionContext): Future[Unit] = {
    FileIO.deleteRecursively(buildDirPath)
  }

  def build(buildType: BuildType)(implicit ec: ExecutionContext): Future[Unit] = {
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
      _ <- assets(config, buildType)
      PostsResult(posts) <- postStore ? AllOrderedByDate
      _ <- createPostsJsonFile(config, posts.toList)
      _ <- tagTemplater.createTemplates(config, posts.toList)
      res <- evaluateDependentTemplates(config, posts.toList)
    } yield res
  }

  def createPostsJsonFile(config: Config, posts: List[Post])(implicit ec: ExecutionContext): Future[Unit] = {
    import spray.json._
    import models.PostJson._

    val json = posts.toJson.compactPrint
    FileIO.write(json, s"$buildDirPath/${config.postsFile}")
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

  def assets(config: Config, buildType: BuildType)(implicit ec: ExecutionContext): Future[Unit] = {
    assert(new File(buildDirPath).exists())

    val sass: Future[Result] = SassCompiler.compile(workingDirectory, buildType)

    val jsFiles = config.jsFiles.map(file => s"$workingDirectory/$file")
    val jsPath = s"$buildDirPath/${config.jsOutputFile}"
    val js: Future[Result] = JavascriptCompiler.compile(jsFiles, jsPath)

//    val img = Future.sequence(config.imagesToOptimize.map(i => ImageUtils.compress(s"$workingDirectory/$i", s"$buildDirPath/$i")))

    Future.sequence(List(sass, js)).map(_ => println("Assets Complete"))
  }

  def make(buildType: BuildType)(implicit ec: ExecutionContext): Future[Unit] = clean.flatMap(_ => build(buildType))

  def server(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    val server = new WebServer(buildDirPath, port = port)
    server.start
  }

  def run(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = make(DevelopmentBuild).flatMap(_ => server(port))

}