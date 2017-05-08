package files.assets

import java.io.File
import java.nio.file.{Files, Paths}

import com.sksamuel.scrimage.{Format, FormatDetector, Image}
import com.sksamuel.scrimage.nio.{ImageWriter, JpegWriter, PngWriter}
import files.FileCrawler.{Failure, Result, Success}
import files.FileIO

import scala.concurrent.{ExecutionContext, Future}


object ImageUtils {

  def compress(inputImage: String, outputImage: String)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      try {
        // FIXME: PNGs lose their transparency
        implicit val writer =
          if (FileIO.extension(inputImage) == "png") PngWriter().withCompression(8)
          else JpegWriter().withCompression(50)

        val image = new File(inputImage)
        Image.fromFile(image).output(outputImage)
        println(s"ImageUtils.compress: CREATED $outputImage")

        Success
      } catch {
        case e: Exception =>
          println(s"ImageUtils.compress: ERROR $inputImage - ${e.getMessage}")
          Failure(e.getMessage)
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
