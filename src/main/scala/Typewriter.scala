import java.nio.file.{Files, Path, Paths}

import files.FileIO
import files.assets.{ImageUtils, JavascriptCompiler, SassCompiler}
import parsers.PostParser
import server.WebServer
import templaters.PostTemplater

import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration
import scala.io.StdIn


object Typewriter extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val postFile = FileIO.read("/Users/mlopezva/Desktop/codemunity/tutorials/intellij-setup-for-scala.md")

  val postFuture = postFile flatMap { fileContents =>
    PostParser.parseFileContents(fileContents)
  }

  val templateFuture = postFuture flatMap { case (post, _) =>
    new PostTemplater("/Users/mlopezva/Desktop/codemunity/templates/tutorial").createPostTemplate(post) map { contents =>
      (post, contents)
    }
  }

  val template = templateFuture flatMap { case (post, templateContents) =>
    val filepath = s"/Users/mlopezva/Desktop/codemunity/tutorials/${post.slug}.html"
    FileIO.write(templateContents, filepath)
  }

  val sass = SassCompiler.compile("/Users/mlopezva/Desktop/codemunity")

  // Order matters
  val jsFiles = List(
    "/Users/mlopezva/Desktop/codemunity/assets/js/skel.min.js",
    "/Users/mlopezva/Desktop/codemunity/assets/js/jquery.min.js",
    "/Users/mlopezva/Desktop/codemunity/assets/js/jquery.scrollex.min.js",
    "/Users/mlopezva/Desktop/codemunity/assets/js/util.js",
    "/Users/mlopezva/Desktop/codemunity/assets/js/main.js"
  )

  val js = JavascriptCompiler.compile(jsFiles, "/Users/mlopezva/Desktop/codemunity/assets/js/compiled.js")

  // Will only output JPEGs
  val imgs = List (
    ("/Users/mlopezva/Desktop/codemunity/images/bg_original.jpg", "/Users/mlopezva/Desktop/codemunity/images/bg.jpg"),
    ("/Users/mlopezva/Desktop/codemunity/images/elm-book-cover_original.png", "/Users/mlopezva/Desktop/codemunity/images/elm-book-cover.png"),
    ("/Users/mlopezva/Desktop/codemunity/images/akka-book-cover_original.png", "/Users/mlopezva/Desktop/codemunity/images/akka-book-cover.png")
  )

  val img = Future.sequence(imgs.map((paths) => ImageUtils.compress(paths._1, paths._2)))

  val f = Future.sequence(List(sass, js, img, template))

  Await.result(f, Duration.Inf)
  println(f)


  val server = new WebServer("/Users/mlopezva/Desktop/codemunity")
  val binding = Await.result(server.start, Duration.Inf)

  println(s"Started server at ${server.host}:${server.port}, press enter to kill server")
  StdIn.readLine()
  server.stop

//  val exts = List(".png", ".jpg", ".jpeg", ".css", ".html", ".beard", ".js")
//
//  val stream: Stream[Path] = FileHelper.walk("/Users/mlopezva/Desktop/codemunity", exts)
//  stream.foreach(println)
}