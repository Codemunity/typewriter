package models


import net.jcazevedo.moultingyaml.{DefaultYamlProtocol, YamlFormat, YamlString, YamlValue}
import com.github.nscala_time.time.Imports._


case class Post(
                 title: String,
                 creationDate: DateTime,
                 description: String,
                 content: Option[String],
                 tags: List[String],
                 authorFilename: String,
                 coverImage: String,
                 slug: String,
                 ignored: Boolean = true
               ) {

//  lazy val author: Author


}

object PostFormat extends DefaultYamlProtocol {
  implicit val postFormat = yamlFormat9(Post)
}