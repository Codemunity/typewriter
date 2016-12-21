package parsers

import laika.api.Transform
import laika.parse.markdown.Markdown
import laika.render.HTML

import scala.concurrent.{ExecutionContext, Future}


trait ModelParser[T] {

  def parseFile(inputFilepath: String, outputFilePath: String)(implicit executor: ExecutionContext): Future[T] = {
    Future {
      val (yaml, markdown) = splitFileContents(inputFilepath)
      val model = yamlToModel(yaml.get)
      createPage(markdown.get, outputFilePath)
      configModel(model, markdown.get)
    }
  }

  def splitFileContents(fileContents: String): (Option[String], Option[String]) = {
    val split = fileContents.split("---")
    if (split.length == 3) {
      (Some(split(1)), Some(split(2)))
    }
    else (None, None)
  }

  def createPage(markdown: String, filepath: String): Unit = {
    Transform from Markdown to HTML fromString markdown toFile filepath
  }

  def configModel(model: T, markdown: String): T

  def yamlToModel(yaml: String): T

}
