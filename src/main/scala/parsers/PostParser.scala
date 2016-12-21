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
object PostParser extends ModelParser[Post] {

  override def yamlToModel(yaml: String): Post = {
    yaml.stripMargin.parseYaml.convertTo[Post]
  }

  override def configModel(model: Post, markdown: String): Post = {
    model.copy(content = Some(markdown))
  }
}
