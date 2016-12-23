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
               ) extends Mappable {

//  lazy val author: Author
  override def toMap: Map[String, Object] = {
    val (title, creationDate, description, content, tags, authorFilename, coverImage, slug, _) = Post.unapply(this).get
    Map(
      "title" -> title,
      "creationDate" -> creationDate,
      "description" -> description,
      "content" -> content,
      "tags" -> tags,
      "authorFilename" -> authorFilename,
      "coverImage" -> coverImage,
      "slug" -> slug
    )
  }
}

object PostFormat extends DefaultYamlProtocol {
  implicit val postFormat = yamlFormat9(Post)
}