package stores

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import models.Post
import org.scalatest.{MustMatchers, WordSpecLike}
import stores.PostStore._
import utils.StopSystemAfterAll
import com.github.nscala_time.time.Imports._


/**
  * Created by mlopezva on 11/20/16.
  */
class PostStoreSpec extends TestKit(ActorSystem("testPosts"))
  with WordSpecLike
  with MustMatchers
  with ImplicitSender
  with StopSystemAfterAll {

  def createPost(title: String, creationDate: DateTime = DateTime.now) = Post(
    title = title,
    creationDate = creationDate,
    description = "description",
    content = Some("content"),
    tags = List("tag1", "tag2"),
    authorFilename = "yodawg",
    coverImage = "coverImage",
    slug = "slug",
    ignored = false
  )

  "A PostStore" must {

    "have an empty post list at the beginning" in {
      val postStore = system.actorOf(PostStore.props)

      postStore ! All

      expectMsg(PostsResult(Vector.empty))
    }

    "add new posts" in {
      val postStore = system.actorOf(PostStore.props)

      val post1 = createPost("title1")

      postStore ! Add(post1)
      postStore ! All
      expectMsg(PostsResult(Vector(post1)))

      val post2 = createPost("title2")
      postStore ! Add(post2)
      postStore ! All
      expectMsg(PostsResult(Vector(post1, post2)))
    }

    "return a list posts ordered by their creation date, the most recent first" in {
      val postStore = system.actorOf(PostStore.props)

      val post1 = createPost("title1")
      postStore ! Add(post1)
      val post2 = createPost("title2", DateTime.now.plus(1000000000))
      postStore ! Add(post2)
      val post3 = createPost("title3", DateTime.now.minus(1000000000))
      postStore ! Add(post3)

      postStore ! AllOrderedByDate
      expectMsg(PostsResult(Vector(post2, post1, post3)))
    }

  }

}
