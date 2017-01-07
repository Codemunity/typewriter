package templaters

import java.nio.file.Paths

import de.zalando.beard.renderer._
import models.Post

import scala.concurrent.{ExecutionContext, Future}

class PostTemplater(val pageTemplater: PageTemplater) {

  def createPostTemplate(post: Post)(implicit ec: ExecutionContext): Future[String] = {
    val context: Map[String, Map[String, Object]] = Map("post" -> post.toMap)
    pageTemplater.createPageTemplate(context)
  }

}