package parsers

import models.Config
import net.jcazevedo.moultingyaml._

/**
  * Created by mlopezva on 4/24/17.
  */
object ConfigParser extends ModelParser[Config] {

  override def yamlToModel(yaml: String): Config = {
    yaml.stripMargin.parseYaml.convertTo[Config]
  }

  override def configModel(model: Config, markdown: String): Config = model
}