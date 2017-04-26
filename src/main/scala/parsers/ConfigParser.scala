package parsers

import models.Config
import models.ConfigFormat._
import net.jcazevedo.moultingyaml._


object ConfigParser extends ModelParser[Config] {

  override def yamlToModel(yaml: String): Config = {
    yaml.stripMargin.parseYaml.convertTo[Config]
  }

  override def configModel(model: Config, markdown: String): Config = model
}