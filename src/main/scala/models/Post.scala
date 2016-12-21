package models


import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat, YamlString, YamlValue}
import com.github.nscala_time.time.Imports._


case class Post(
                 title: String,
                 creationDate: DateTime,
                 description: String,
                 content: Option[String],
                 tags: List[String],
                 sanitizedContent: Option[String],
                 authorFilename: String,
                 coverImage: String,
                 slug: Option[String],
                 ignored: Boolean = true
               ) {

//  lazy val author: Author


}

object PostFormat extends DefaultYamlProtocol {
  implicit val postFormat = yamlFormat10(Post)
}