package files.handlers

import java.nio.file.Paths

import files.FileIO

import scala.concurrent.{ExecutionContext, Future}
import parsers.PostParser
import templaters.PostTemplater


trait FileHandler[T] {

  def handleFile(filepath: String, destination: String)(implicit ec: ExecutionContext): Future[T]

}

object CopyHandler extends FileHandler[Unit] {

  override def handleFile(filepath: String, destination: String)(implicit ec: ExecutionContext): Future[Unit] = {
    FileIO.copy(filepath, destination)
  }

}

class TemplateHandler(val templatePath: String) extends FileHandler[Unit] {

  override def handleFile(filepath: String, destination: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val filename = Paths.get(filepath).getFileName.toString
    val path = s"$destination/$filename"
    val templater = new PostTemplater(templatePath)

    for {
      contents <- FileIO.read(filepath)
      (post, _) <- PostParser.parseFileContents(contents)
      template <- templater.createPostTemplate(post)
      result <- FileIO.write(template, path)
    } yield result
  }
}