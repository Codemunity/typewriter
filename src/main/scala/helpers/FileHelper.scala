package helpers

import java.io.{File, PrintWriter}

import scala.concurrent.{ExecutionContext, Future}
import scala.io.Source


object FileHelper {

  def read(filepath: String)(implicit executor: ExecutionContext): Future[String] = {
    Future{
      Source.fromFile(filepath).mkString
    }
  }

  def write(fileContents: String, filepath: String)(implicit executor: ExecutionContext): Future[Unit] = {
    Future {
      val writer = new PrintWriter(new File(filepath))
      writer.write(fileContents)
      writer.close()
    }
  }

}
