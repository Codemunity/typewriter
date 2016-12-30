package files.assets

import java.io.File

import com.sksamuel.scrimage.Image
import com.sksamuel.scrimage.nio.JpegWriter
import files.FileCrawler.{Failure, Result, Success}

import scala.concurrent.{ExecutionContext, Future}


object ImageUtils {

  def compress(inputImage: String, outputImage: String, compression: Int = 50)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      try {
        implicit val writer = JpegWriter().withCompression(compression)

        val image = new File(inputImage)
        Image.fromFile(image).output(outputImage)

        Success
      } catch {
        case e: Exception => Failure(e.getMessage)
      }
    }
  }

  def resize(inputImage: String, outputImage: String, width: Int, height: Int)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      try {
        val image = new File(inputImage)
        Image.fromFile(image).scaleTo(width, height).output(outputImage)

        Success
      } catch {
        case e: Exception => Failure(e.getMessage)
      }
    }
  }

}
