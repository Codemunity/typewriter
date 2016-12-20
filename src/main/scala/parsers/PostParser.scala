package parsers

import laika.api.Transform
import laika.parse.markdown.Markdown
import laika.render.HTML
import models.Post
import net.jcazevedo.moultingyaml._
import models.PostFormat._

import scala.concurrent.Future

/**
  * Created by mlopezva on 11/20/16.
  */
object PostParser {

  def parsePostFile(inputFilepath: String, outputFilePath: String): Future[Post] = {
    Future {
      val (yaml, markdown) = splitFileContents(inputFilepath)
      val post = yamlToPost(yaml.get)
      createPostPage(markdown.get, outputFilePath)
      post.copy(content = markdown)
    }
  }

  def splitFileContents(fileContents: String): (Option[String], Option[String]) = {
    val split = fileContents.split("---")
    if (split.length == 3) {
      (Some(split(1)), Some(split(2)))
    }
    else (None, None)
  }

  def yamlToPost(yaml: String): Post = {
    yaml.stripMargin.parseYaml.convertTo[Post]
  }

  def createPostPage(markdown: String, filepath: String): Unit = {
    Transform from Markdown to HTML fromString markdown toFile filepath
  }

}
