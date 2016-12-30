package parsers

import org.commonmark.parser.Parser
import org.commonmark.renderer.html.HtmlRenderer

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
    val parser = Parser.builder().build()
    val document = parser.parse(markdown)
    val renderer = HtmlRenderer.builder().build()
    renderer.render(document)
  }

  def configModel(model: T, markdown: String): T

  def yamlToModel(yaml: String): T

}
