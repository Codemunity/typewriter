package files

import java.io.File

import files.handlers.{CopyHandler, PageTemplateHandler, PostTemplateHandler}

import scala.concurrent.{ExecutionContext, Future}


class FileCrawler(val workingDirectory: String) {

  import FileCrawler._

  // TODO: Refactor into config
  private val postTemplatePath = s"$workingDirectory/templates/tutorial"
  private val applicationTemplatePath = s"$workingDirectory/templates/application"
  private val ignoredFiles = List(
    "afdesigns",
    "build",
    "config",
    "templates",
    "assets/sass",
    "assets/js",
    "assets/config",
    "images/assets",
    "install_and_generate.sh",
    "generate.sh"
  )

  val postTemplateHandler = new PostTemplateHandler(workingDirectory, postTemplatePath)

  def crawl(inputDirectory: String, outputDirectory: String)(implicit ec: ExecutionContext): Future[Result] = {
    println()
    println(s"Crawling $inputDirectory... Outputting to $outputDirectory")
    val files = FileIO.files(inputDirectory, ignorePredicate(inputDirectory))
    val f = files.map {
      case file if file.isDirectory => {
        val newFile = s"$outputDirectory/${file.getName}"
        for {
          _ <- FileIO.mkdir(newFile)
          result <- crawl(file.getAbsolutePath, newFile)
        } yield result
      }
      case file if FileIO.extension(file) == "beard" => {
        // Beard adds the extension by default
        println(s"Parsing Template: ${file.getName}")
        val filename = FileIO.fileNameWithoutExtension(file.getName)
        PageTemplateHandler.handleFile(workingDirectory, s"$inputDirectory/$filename", outputDirectory)
      }
      case file if FileIO.extension(file) == "md" =>
        println(s"Parsing Tutorial: ${file.getName}")
        postTemplateHandler.handleFile(s"$inputDirectory/${file.getName}", outputDirectory)
      case file =>
        CopyHandler.handleFile(s"$inputDirectory/${file.getName}", outputDirectory)
    }
    Future.sequence(f).map(_ => Success)
  }

  def ignorePredicate(directory: String)(file: File): Boolean = {
    val ignoredFilesFromDir = ignoredFiles.map(f => s"$workingDirectory/$f")
    !ignoredFilesFromDir.contains(s"$directory/${file.getName}") && !file.getName.startsWith(".")
  }

}

object FileCrawler {

  trait Result

  case object Success extends Result

  case class Failure(message: String) extends Result

  def reduce(res1: Result, res2: Result): Result = (res1, res2) match {
    case (Failure(msg1), Failure(msg2)) => Failure(s"Failure1: $msg1 \n Failure2: $msg2")
    case (Failure(msg), _) => Failure(msg)
    case (_, Failure(msg)) => Failure(msg)
    case _ => Success
  }

}