package models


import java.sql.Date

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import net.jcazevedo.moultingyaml.DefaultYamlProtocol
import com.github.nscala_time.time.Imports._
import spray.json.{DefaultJsonProtocol, DeserializationException, JsString, JsValue, RootJsonFormat}


case class Post(
                 title: String,
                 creationDate: DateTime,
                 description: String,
                 content: Option[String],
                 tags: List[String],
                 authorFilename: String,
                 coverImage: String,
                 slug: String,
                 ignored: Boolean
               ) extends Mappable {

//  lazy val author: Author
  override def toMap: Map[String, Object] = {
    val (title, creationDate, description, content, tags, authorFilename, coverImage, slug, _) = Post.unapply(this).get
    Map(
      "title" -> title,
      "creationDate" -> creationDate.toString("d MMMM, yyyy"),
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

object PostJson extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val postFormat = jsonFormat9(Post.apply)

  implicit object DateTimeJsonFormat extends RootJsonFormat[DateTime] {
    def write(dateTime: DateTime) = JsString(dateTime.toString)

    def read(value: JsValue) = value match {
      case JsString(dateStr) => DateTime.parse(dateStr)
      case _ => throw new DeserializationException("Date expected")
    }
  }
}
