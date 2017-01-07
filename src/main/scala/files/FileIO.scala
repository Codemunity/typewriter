package files

import java.io.{File, PrintWriter}
import java.nio.file.{Files, Path, Paths}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future}
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

  def delete(filepath: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val fileTemp = new File(filepath)
      if (fileTemp.exists) fileTemp.delete
    }
  }

  def deleteRecursively(filepath: String)(implicit ec: ExecutionContext): Future[Unit] = {
    val file = new File(filepath)
    deleteRecursively(file)
  }
  private def deleteRecursively(file: File)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      if (file.isDirectory) {
        val res = files(file.getAbsolutePath).map(deleteRecursively)
        Await.result(Future.sequence(res), Duration.Inf)
      }
      file.delete
    }
  }

  def mkdir(filepath: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val dir = new File(filepath)
      if (!dir.exists) dir.mkdir()
    }
  }

  def copy(filepath: String, destinationDir: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future {
      val path = Paths.get(filepath)
      val newFilepath = Paths.get(s"$destinationDir/${path.getFileName}")

      Files.copy(path, newFilepath)
    }
  }

  def files(directory: String, predicate: (File) => Boolean = (_) => true): List[File] = {
    val file = new File(directory)
    if (file.isDirectory) file.listFiles().filter(predicate).toList
    else Nil
  }

  def extension(fileName: String): String = fileName.split("\\.").last

  def extension(file: File): String = extension(file.getAbsolutePath)

  def fileNameWithoutExtension(filepath: String): String = {
    val ext = extension(filepath)
    Paths.get(filepath).getFileName.toString.replaceFirst(s".$ext", "")
  }

  def difference(parent: String, child: String): String = {
    val parentFile = new File(parent)
    val childFile = new File(child)

    if (parentFile.getName == childFile.getParentFile.getName) childFile.getName
    else difference(parent, childFile.getParent) + "/" + childFile.getName
  }

}
