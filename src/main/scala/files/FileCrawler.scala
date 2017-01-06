package files

import java.io.File

import scala.concurrent.{ExecutionContext, Future}


object FileCrawler {

  trait Result
  case object Success extends Result
  case class Failure(message: String) extends Result

  private val ignoredFiles = List(
    "LICENSE.txt",
    "README.txt",
    "afdesigns",
    "build",
    "config",
    "templates/tutorial.beard",
    "assets/sass",
    "assets/js",
    "assets/config",
    "images/bg_original.jpg",
    "images/elm-book-cover_original.png",
    "images/akka-book-cover_original.png"
  )

  def crawl(inputDirectory: String, outputDirectory: String)(implicit ec: ExecutionContext): Future[Result] = {
    val files = FileIO.files(inputDirectory, ignorePredicate(inputDirectory))
    println(files)
    files.map {
      case file if file.isDirectory => {

        // Create folder
        crawl(file.getAbsolutePath, outputDirectory)
      }
      case file if FileIO.extension(file) == "beard" => println(s"handle ${file.getName} as BEARD")
      case file if FileIO.extension(file) == "md" => println(s"handle ${file.getName} as MARKDOWN")
      case file => println(s"handle ${file.getName} as COPY")
    }

    Future(Success)
  }

  def ignorePredicate(directory: String)(file: File): Boolean = {
    val ignoredFilesFromDir = ignoredFiles.map(f => s"$directory/$f")
//    println(ignoredFilesFromDir)
//    println()
//    println(s"$directory/${file.getName}")
    !ignoredFilesFromDir.contains(s"$directory/${file.getName}") && !file.getName.startsWith(".")
  }

}
