import java.nio.file.Files

import helpers.FileHelper
import parsers.PostParser
import templaters.PostTemplater

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * Created by mlopezva on 11/15/16.
  */
object Typewriter extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val postFile = FileHelper.read("/Users/mlopezva/Desktop/codemunity/tutorials/intellij-setup-for-scala.md")

  val postFuture = postFile flatMap { fileContents =>
    PostParser.parseFileContents(fileContents)
  }

  val template = postFuture flatMap { case (post, _) =>
    PostTemplater.parseTemplate("/Users/mlopezva/Desktop/codemunity/templates/tutorial.ssp", post) map { contents =>
      (post, contents)
    }
  }

  val f = template flatMap { case (post, templateContents) =>
    val filepath = s"/Users/mlopezva/Desktop/codemunity/tutorials/${post.slug}.html"
    FileHelper.write(templateContents, filepath)
  }

  Await.result(f, Duration.Inf)
  println(f)

}