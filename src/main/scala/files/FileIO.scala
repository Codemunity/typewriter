package files

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Paths}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source


object FileIO {

  def read(filepath: String)(implicit executor: ExecutionContext): Future[String] = {
    Future{
      Source.fromFile(filepath).mkString
    }
  }

  def write(fileContents: String, filepath: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val writer = new PrintWriter(new File(filepath))
      writer.write(fileContents)
      writer.close()
    }
  }

  def copy(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val path = Paths.get(filepath)
      val newFilepath = s"$destinationDir/${path.getFileName}"

      if (Files.isDirectory(path)) {
        val newPath = Paths.get(newFilepath)
        Files.createDirectory(newPath)
      } else {
        for {
          contents <- FileIO.read(filepath)
          result <- FileIO.write(contents, newFilepath)
        } yield result
      }
    }
  }

  def files(directory: String, predicate: (File) => Boolean = (_) => true): List[File] = {
    val file = new File(directory)
    if (file.isDirectory) file.listFiles().filter(predicate).toList
    else Nil
  }

  def extension(fileName: String): Option[String] = {
    if (Files.isDirectory(Paths.get(fileName))) None
    else Some(fileName.split("\\.").last)
  }

}
