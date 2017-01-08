import java.io.File

import akka.http.scaladsl.Http.ServerBinding
import files.FileCrawler.Result
import files.assets.{ImageUtils, JavascriptCompiler, SassCompiler}
import files.handlers.FileHandler
import files.{FileCrawler, FileIO}
import server.WebServer

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}


class Typewriter(val workingDirectory: String) {

  // TODO: Refactor into config
  val buildDirName = "build"
  val jsFiles = List(
    s"$workingDirectory/assets/js/skel.min.js",
    s"$workingDirectory/assets/js/jquery.min.js",
    s"$workingDirectory/assets/js/jquery.scrollex.min.js",
    s"$workingDirectory/assets/js/util.js",
    s"$workingDirectory/assets/js/main.js"
  )
  val configJsPath = "assets/js/compiled.js"


  val buildDirPath = s"$workingDirectory/$buildDirName"
  val jsPath = s"$buildDirPath/$configJsPath"


  // TODO: Refactor into config
  val imgs = List(
    (s"$workingDirectory/images/bg.jpg", s"$buildDirPath/images/bg.jpg"),
    (s"$workingDirectory/images/elm-book-cover.png", s"$buildDirPath/images/elm-book-cover.png"),
    (s"$workingDirectory/images/akka-book-cover.png", s"$buildDirPath/images/akka-book-cover.png")
  )


  def clean(implicit ec: ExecutionContext): Future[Unit] = {
    FileIO.deleteRecursively(buildDirPath)
  }

  def build(implicit ec: ExecutionContext): Future[Unit] = {
    val fileCrawler = new FileCrawler(workingDirectory)
    for {
      _ <- FileIO.mkdir(buildDirPath)
      _ <- fileCrawler.crawl(workingDirectory, buildDirPath)
      assetsResult <- assets
    } yield assetsResult
  }

  def assets(implicit ec: ExecutionContext): Future[Result] = {
    assert(new File(buildDirPath).exists())

    val sass: Future[Result] = SassCompiler.compile(workingDirectory)
    val js: Future[Result] = JavascriptCompiler.compile(jsFiles, jsPath)
    val img:  Future[Result] =
      Future.sequence(imgs.map((paths) => ImageUtils.compress(paths._1, paths._2))).map(_.reduce(FileCrawler.reduce))

    Future.sequence(List(sass, js, img)).map(_.reduce(FileCrawler.reduce))
  }

  def make(implicit ec: ExecutionContext): Future[Unit] = {
    for {
      _ <- clean
      result <- build
    } yield result
  }

  def server(port: Int = 9000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    val server = new WebServer(buildDirPath, port = port)
    server.start
  }

  def run(port: Int = 9000)(implicit ec: ExecutionContext): Future[ServerBinding] = {
    for {
      _ <- make
      serverResult <- server(port)
    } yield serverResult
  }

}