package crawlers

import java.io.File

import scala.concurrent.{ExecutionContext, Future}


object FileCrawler {

  trait Result
  case object Success extends Result
  case class Failure(message: String) extends Result

  private val templateExts = List("md")
  private val copyExts = List("html", "css", "js", "png", "jpeg", "jpg")
  private val ignoreFiles = List("")

  def crawl(directory: String)(implicit ec: ExecutionContext): Future[Result] = {
    Future(Success)
  }

}
