package models

import net.jcazevedo.moultingyaml.DefaultYamlProtocol


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
                   jsOutputFile: String
                 ) {
  def allIgnoredFiles = ignoredFiles ++ jsFiles ++ postListDependentTemplates
}

object Config {
  val filename = "typewriter.yaml"
  val buildDirName = "build"
}

object ConfigFormat extends DefaultYamlProtocol {
  implicit val configFormat = yamlFormat5(Config.apply)
}