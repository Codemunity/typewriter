package parsers

import models.Author
import models.AuthorFormat._
import net.jcazevedo.moultingyaml._

/**
  * Created by mlopezva on 12/21/16.
  */
object AuthorParser extends ModelParser[Author] {

  override def yamlToModel(yaml: String): Author = {
    yaml.stripMargin.parseYaml.convertTo[Author]
  }

  override def configModel(model: Author, markdown: String): Author = model
}