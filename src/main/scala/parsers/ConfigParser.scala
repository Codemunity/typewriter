package parsers

import models.Config
import models.ConfigJson._
import spray.json._


object ConfigParser {

  def jsonToModel(json: String): Config = {
    configFormat.read(json.parseJson)
  }
}