package files.handlers

import java.io.File
import java.nio.file.Paths

import files.FileIO

import scala.concurrent.{ExecutionContext, Future}
import parsers.PostParser
import templaters.{PageTemplater, PostTemplater}


object FileHandler {



}

object CopyHandler {

  def handleFile(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {
    println()
    if (filepath.contains("font")) println(s"CopyHandle.handlerFile: F: $filepath - D: $destinationDir")
    FileIO.copy(filepath, destinationDir)
  }

}

class PostTemplateHandler(workingDirectory: String, val templatePath: String) {

  def handleFile(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {

    val filename = Paths.get(filepath).getFileName.toString
    val path = s"$destinationDir/$filename"
    val pageTemplater = new PageTemplater(workingDirectory, templatePath)
    val postTemplater = new PostTemplater(pageTemplater)

    for {
      contents <- FileIO.read(filepath)
      (post, _) <- PostParser.parseFileContents(contents)
      template <- postTemplater.createPostTemplate(post)
      result <- FileIO.write(template, path)
    } yield result
  }
}


object PageTemplateHandler {

  def handleFile(workingDirectory: String, filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {

    val filename = FileIO.fileNameWithoutExtension(filepath)
    val destinationFile = s"$destinationDir/$filename.html"

    for {
      template <- PageTemplater.createPageTemplate(workingDirectory, filepath)
      result <- FileIO.write(template, destinationFile)
    } yield result
  }
}