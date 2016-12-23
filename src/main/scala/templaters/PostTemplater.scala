package templaters

import de.zalando.beard.renderer._
import models.Post

import scala.concurrent.{ExecutionContext, Future}

class PostTemplater(val workingDirectory: String) {

  val loader = new FileTemplateLoader(
    directoryPath = workingDirectory,
    templateSuffix = ".beard"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer(templateCompiler)

  def parseTemplate(templateName: String, post: Post)(implicit ec: ExecutionContext): Future[String] = {
    Future {
      val template = templateCompiler.compile(TemplateName(templateName)).get
      val context: Map[String, Map[String, Object]] = Map("post" -> post.toMap)

      val result = renderer.render(template,
        StringWriterRenderResult(),
        context,
        None)
      result.toString
    }
  }

}