package models


case class Config(
                   // Template for a single post
                   postTemplate: String,
                   // Templates which require the list of posts
                   postListDependentTemplates: List[String],
                   // The directory where your posts are
                   postsDirectory: String,
                   // Your application's base template
                   baseTemplate: Option[String],
                   // Ignored files and directories
                   ignoredFiles: List[String]
                 )

object Config {
  val filename = "Typewriter.yaml"
}