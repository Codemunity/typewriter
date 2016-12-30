package server

import akka.http.scaladsl.model._
import akka.actor.ActorSystem
import akka.event.Logging
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives._
import akka.stream.ActorMaterializer
import java.nio.file.{Files, Paths}

import akka.http.scaladsl.Http.ServerBinding
import akka.http.scaladsl.model.StatusCodes._
import helpers.FileHelper

import scala.concurrent.Future



class WebServer(val workingDirectory: String, val host: String = "localhost", val port: Int = 9000) {

  implicit val system = ActorSystem()
  implicit val executor = system.dispatcher
  implicit val materializer = ActorMaterializer()

  val logger = Logging(system, getClass)

  def start: Future[ServerBinding] = {
    Http().bindAndHandle(routes, host, port)
  }

  def stop = system.terminate

  val routes =
    get {
      entity(as[HttpRequest]) { requestData =>
        complete {

          val fullPath = requestData.uri.path.toString match {
            case "" => s"$workingDirectory/index.html"
            case "/" => s"$workingDirectory/index.html"
            case url if url.contains(".") => s"$workingDirectory/$url"
            case url => s"$workingDirectory/$url.html"
          }

          println(s"retrieving: $fullPath")

          val ext = FileHelper.extension(fullPath).getOrElse("")

          val mediaType = MediaTypes.forExtensionOption(ext).getOrElse(MediaTypes.`text/plain`)
          val contentType = ContentType(mediaType, () => HttpCharsets.`UTF-8`)
          val byteArray = Files.readAllBytes(Paths.get(fullPath))
          HttpResponse(OK, entity = HttpEntity(contentType, byteArray))
        }
      }
    }


}
