package parsers

import laika.api.Transform
import laika.parse.markdown.Markdown
import laika.render.HTML

import scala.concurrent.{ExecutionContext, Future}


trait ModelParser[T] {

  def parseFileContents(fileContents: String)(implicit executor: ExecutionContext): Future[(T, String)] = {
    Future {
      val (yaml, markdown) = splitFileContents(fileContents)
      val model = yamlToModel(yaml.get)
      val parsedMarkdown = parseMarkdown(markdown.get)
      val newModel = configModel(model, parsedMarkdown)
      (newModel, parsedMarkdown)
    }
  }

  def splitFileContents(fileContents: String): (Option[String], Option[String]) = {
    val split = fileContents.split("---")
    if (split.length == 3) {
      (Some(split(1)), Some(split(2)))
    }
    else (None, None)
  }

  def parseMarkdown(markdown: String): String = {
    Transform from Markdown to HTML fromString markdown toString
  }

  def configModel(model: T, markdown: String): T

  def yamlToModel(yaml: String): T

}
