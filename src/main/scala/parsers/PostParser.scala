package parsers

import models.Post
import net.jcazevedo.moultingyaml._
import models.PostFormat._

import scala.concurrent.Future


object PostParser extends ModelParser[Post] {

  override def yamlToModel(yaml: String): Post = {
    yaml.stripMargin.parseYaml.convertTo[Post]
  }

  override def configModel(model: Post, markdown: String): Post = {
//    val sanitizedContent = scala.xml.parsing.XhtmlParser
    model.copy(content = Some(markdown))
  }
}