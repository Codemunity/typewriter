import java.nio.file.{Files, Path, Paths}

import files.assets.SassCompiler
import helpers.FileHelper
import parsers.PostParser
import server.WebServer
import templaters.PostTemplater

import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn


object Typewriter extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

//  val postFile = FileHelper.read("/Users/mlopezva/Desktop/codemunity/tutorials/intellij-setup-for-scala.md")
//
//  val postFuture = postFile flatMap { fileContents =>
//    PostParser.parseFileContents(fileContents)
//  }
//
//  val template = postFuture flatMap { case (post, _) =>
//    new PostTemplater("/Users/mlopezva/Desktop/codemunity/templates/tutorial").createPostTemplate(post) map { contents =>
//      (post, contents)
//    }
//  }
//
//  val f = template flatMap { case (post, templateContents) =>
//    val filepath = s"/Users/mlopezva/Desktop/codemunity/tutorials/${post.slug}.html"
//    FileHelper.write(templateContents, filepath)
//  }

  val f = SassCompiler.compile("/Users/mlopezva/Desktop/codemunity")

  Await.result(f, Duration.Inf)
  println(f)


//  val server = new WebServer("/Users/mlopezva/Desktop/codemunity")
//  val binding = Await.result(server.start, Duration.Inf)
//
//  println(s"Started server at ${server.host}:${server.port}, press enter to kill server")
//  StdIn.readLine()
//  server.stop

//  val exts = List(".png", ".jpg", ".jpeg", ".css", ".html", ".beard", ".js")
//
//  val stream: Stream[Path] = FileHelper.walk("/Users/mlopezva/Desktop/codemunity", exts)
//  stream.foreach(println)
}