import java.io.File

import akka.http.scaladsl.Http.ServerBinding
import files.FileCrawler.Result
import files.assets.{ImageUtils, JavascriptCompiler, SassCompiler}
import files.handlers.FileHandler
import files.{FileCrawler, FileIO}
import models.Config
import parsers.ConfigParser
import server.WebServer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


class Typewriter(val workingDirectory: String) {

  val buildDirPath = s"$workingDirectory/${Config.buildDirName}"

  def loadConfig(implicit ec: ExecutionContext): Future[Config] = {
    val configPath = s"$workingDirectory/${Config.filename}"
    for {
      contents <- FileIO.read(configPath)
    } yield {
      println(contents)
      ConfigParser.yamlToModel(contents)
    }
  }

  def clean(implicit ec: ExecutionContext): Future[Unit] = {
    FileIO.deleteRecursively(buildDirPath)
  }

  def build(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      config <- loadConfig
      _ <- FileIO.mkdir(buildDirPath)
      crawler <- Future.successful(new FileCrawler(workingDirectory, config))
      _ <- crawler.crawl(workingDirectory, buildDirPath)
      assetsResult <- assets(config)
    } yield assetsResult
  }

  def assets(config: Config)(implicit ec: ExecutionContext): Future[Result] = {
    assert(new File(buildDirPath).exists())

    val sass: Future[Result] = SassCompiler.compile(workingDirectory)

    val jsFiles = config.jsFiles.map(file => s"$workingDirectory/$file")
    val jsPath = s"$buildDirPath/${config.jsOutputFile}"
    val js: Future[Result] = JavascriptCompiler.compile(jsFiles, jsPath)
//    val img:  Future[Result] =
//      Future.sequence(imgs.map((paths) => ImageUtils.compress(paths._1, paths._2))).map(_.reduce(FileCrawler.reduce))

    Future.sequence(List(sass, js)).map(_.reduce(FileCrawler.reduce))
  }

  def make(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      _ <- clean
      result <- build
    } yield result
  }

  def server(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    val server = new WebServer(buildDirPath, port = port)
    server.start
  }

  def run(port: Int = 5000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    for {
      _ <- make
      serverResult <- server(port)
    } yield serverResult
  }

}