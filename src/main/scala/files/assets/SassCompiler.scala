package files.assets

import java.io.File

import files.FileCrawler.{Failure, Result, Success}
import models.Config
import models.builds.{BuildType, DevelopmentBuild, ProductionBuild}

import scala.concurrent.{ExecutionContext, Future}
import scala.sys.process._


object SassCompiler {

  def compile(configDirectory: String, buildType: BuildType)(implicit ec: ExecutionContext): Future[Result] = {
    Future {
      val dir = new File(configDirectory)
//      val cleanProcess = Process("compass clean", dir)



      val process = buildType match {
        case DevelopmentBuild =>
          println("Compiling SASS with DevelopmentBuild")
          Process(s"node-sass --r assets/sass/ -o ${Config.buildDirName}/assets/css/", dir)
        case ProductionBuild =>
          println("Compiling SASS with ProductionBuild")
          Process(s"node-sass --output-style compressed assets/sass/ -o ${Config.buildDirName}/assets/css/", dir)
      }

      // node-sass -r assets/sass/ -o build/assets/css/

      try {
//        println(s"clean: ${cleanProcess!!}")
        println(s"compile: ${process!!}")

        Success
      } catch {
        case e: Exception => new Failure(e.getMessage)
      }

    }
  }

}
