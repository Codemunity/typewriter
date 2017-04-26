package stores

import akka.actor.{Actor, ActorLogging, Props}
import akka.actor.Actor.Receive
import akka.util.Timeout
import models.Post

/**
  * Created by mlopezva on 11/20/16.
  */

object PostStore {
  def props = Props(new PostStore)

  case object All
  case object AllOrderedByDate
  case object Clear
  case class Add(post: Post)
  case class FindByTitle(title: String)

  case class PostsResult(posts: Vector[Post])
  case class PostResult(post: Option[Post])
}

class PostStore extends Actor with ActorLogging {
  import PostStore._

  var posts = Vector.empty[Post]

  override def receive: Receive = {
    case All => sender() ! PostsResult(posts)
    case AllOrderedByDate => sender() ! PostsResult(posts.sortWith( (p1, p2) => p1.creationDate isAfter p2.creationDate ))
    case Clear => posts = Vector.empty[Post]
    case Add(post) => posts = posts.filterNot(_.title == post.title) :+ post
    case FindByTitle(title) => sender() ! PostResult(posts.find(_.title == title))
  }
}
