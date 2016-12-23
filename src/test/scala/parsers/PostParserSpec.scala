package parsers

import models.Post
import models.PostFormat._
import org.scalatest.{MustMatchers, WordSpecLike}

/**
  * Created by mlopezva on 11/20/16.
  */
class PostParserSpec extends WordSpecLike
  with MustMatchers {

  val postMarkdown =
    """
      ---
      title: First post
      creationDate: 2016-04-24
      description: The first post is about one topic.
      tags: [swift, ios,tdd]
      layout: test/layout
      authorFilename: authors/miguel-lopez.yaml
      slug: my-slug
      coverImage: images/dummy.png
      ignored: false
      ---

      This post is written in **Markdown**.

      ## A header2 is good

      Lists are nice, too:

      - Apples
      - Bananas
      - Pears
    """.stripMargin

  "A PostParser" must {

    "separate the yaml from the post content" in {
      val (yaml, postContents) = PostParser.splitFileContents(postMarkdown)
      yaml mustBe defined
      yaml.get must include ("title: First post")

      postContents mustBe defined
      postContents.get must include ("Markdown")
    }

    "parse the yaml and return a Post with the correct info" in {
      val (yaml, _) = PostParser.splitFileContents(postMarkdown)
      val post: Post = PostParser.yamlToModel(yaml.get)
      post.title must equal ("First post")
      post.tags must equal (List("swift", "ios", "tdd"))
    }

  }

}
