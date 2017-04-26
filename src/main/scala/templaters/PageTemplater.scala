package templaters

import java.nio.file.Paths

import de.zalando.beard.renderer._
import files.FileIO

import scala.concurrent.{ExecutionContext, Future}


class PageTemplater(val workingDirectory: String, val templatePath: String) {

  println(s"PageTemplater w:$workingDirectory - t:$templatePath")
  val templateName: String = FileIO.difference(workingDirectory, templatePath)

  val loader = new FileTemplateLoader(
    directoryPath = workingDirectory,
    templateSuffix = ".beard"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer(templateCompiler)

  def createPageTemplate(context: Map[String, Map[String, Object]] = Map())(implicit ec: ExecutionContext): Future[String] = {
    Future {
      try {
        val template = templateCompiler.compile(TemplateName(templateName)).get

        val result = renderer.render(template,
          StringWriterRenderResult(),
          context,
          None)
        result.toString
      } catch {
        case e: Exception => {
          println(s"ERROR with $templateName at $templatePath")
          throw  e
        }
      }
    }
  }

}

object PageTemplater {
  def createPageTemplate(workingDirectory: String, templatePath: String, context: Map[String, Map[String, Object]] = Map())(implicit ec: ExecutionContext): Future[String] = {
    new PageTemplater(workingDirectory, templatePath).createPageTemplate(context)
  }
}
