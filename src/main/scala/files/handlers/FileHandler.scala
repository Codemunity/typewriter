package files.handlers

import java.io.File
import java.nio.file.Paths

import files.FileIO
import models.Post

import scala.concurrent.{ExecutionContext, Future}
import parsers.PostParser
import templaters.{PageTemplater, PostTemplater}


object CopyHandler {

  def handleFile(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {
    println()
    if (filepath.contains("font")) println(s"CopyHandle.handlerFile: F: $filepath - D: $destinationDir")
    FileIO.copy(filepath, destinationDir)
  }

}

class PostTemplateHandler(workingDirectory: String, val templatePath: String) {

  def handleFile(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Post] = {

    val filename = FileIO.fileNameWithoutExtension(filepath)
    val path = s"$destinationDir/$filename.html"
    val pageTemplater = new PageTemplater(workingDirectory, templatePath)
    val postTemplater = new PostTemplater(pageTemplater)

    for {
      contents <- FileIO.read(filepath)
      (post, _) <- PostParser.parseFileContents(contents)
      template <- postTemplater.createPostTemplate(post)
      result <- FileIO.write(template, path)
    } yield post
  }
}

object PageTemplateHandler {

  def handleFile(workingDirectory: String, filepath: String, destinationDir: String, context: Map[String, Map[String, Object]] = Map())(implicit ec: ExecutionContext): Future[Unit] = {

    val filename = FileIO.fileNameWithoutExtension(filepath)
    val destinationFile = s"$destinationDir/$filename.html"

    println(s"handleFile w:$workingDirectory - f:$filepath - d:$destinationDir")

    for {
      template <- PageTemplater.createPageTemplate(workingDirectory, filepath, context)
      result <- FileIO.write(template, destinationFile)
    } yield result
  }
}