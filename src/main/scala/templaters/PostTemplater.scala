package templaters

import com.github.nscala_time.time.Imports._
import helpers.FileHelper
import models.Post
import org.fusesource.scalate.TemplateEngine
import parsers.PostParser

import scala.concurrent.{Await, ExecutionContext, Future}
import scala.concurrent.duration.Duration
import scala.io.Source


object PostTemplater {

  val engine = new TemplateEngine

  def parseTemplate(filepath: String, post: Post)(implicit ec: ExecutionContext): Future[String] = {
    Future {
      engine.layout(filepath, Map("post" -> post))
    }
  }
}