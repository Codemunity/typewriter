package models

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.DefaultJsonProtocol


case class Config(
                   // Template for a single post
                   postTemplate: String,
                   // Templates which require the list of posts
                   postListDependentTemplates: List[String],
                   // Ignored files and directories
                   ignoredFiles: List[String],
                   // List of JavaScript files to be minified and bundled
                   jsFiles: List[String],
                   // JavaScript output path under working directory, e.g. "assets/js/compiled.js"
                   jsOutputFile: String,

                   imagesToOptimize: List[String],
                   postsFile: String
                 )
object Config {
  val filename = "typewriter.json"
  val buildDirName = "build"

  def allIgnoredFiles(config: Config) =
    config.ignoredFiles ++
      config.jsFiles ++ config.
      postListDependentTemplates ++
      Config.filename
}

//object ConfigFormat extends DefaultYamlProtocol {
//  implicit val configFormat = yamlFormat5(Config.apply)
//}

object ConfigJson extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val configFormat = jsonFormat7(Config.apply)
}