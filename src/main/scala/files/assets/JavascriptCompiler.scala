package files.assets

import java.io.File
import java.nio.file.Paths

import files.FileCrawler.{Failure, Result, Success}

import scala.concurrent.{Await, ExecutionContext, Future}
import com.google.javascript.jscomp.{Compiler, CompilerOptions, JSSourceFile}
import files.FileIO

import scala.concurrent.duration.Duration


object JavascriptCompiler {


  def compile(javascriptDirectory: String, outputPath: String)(implicit ec: ExecutionContext): Future[Result] = {
    val files = FileIO.files(javascriptDirectory, isJavascriptFile).map(_.getAbsolutePath)
    JavascriptCompiler.compile(files, outputPath)
  }

  def compile(files: List[String], outputPath: String)(implicit ec: ExecutionContext): Future[Result] = {
    Future {

      val delete = FileIO.delete(outputPath)
      Await.result(delete, Duration.Inf)

      val compiler = new Compiler

      val result = compiler.compile(Array.empty[JSSourceFile], files.map(JSSourceFile.fromFile).toArray, new CompilerOptions)

      val warnings = result.warnings
      val errors = result.errors

      warnings.foreach(println)

      if (errors.nonEmpty) {
        val msg = errors.map(_.toString).reduce(_ + "\n" + _)
        println(s"JavascriptCompiler: ERROR: $msg")
        Failure(msg)
      } else {

        val source = compiler.toSource

        val dirsCreated = new File(outputPath).getParentFile.mkdirs()
        println(s"JavascriptCompiler: parentCreated: $dirsCreated - ${new File(outputPath).getParentFile}")
        val writeFuture = FileIO.write(source, outputPath).map(_ => Success)

        Await.result(writeFuture, Duration.Inf)
      }
    }
  }

  private def isJavascriptFile(file: File): Boolean = {
    FileIO.extension(file.getName) == "js"
  }
}
