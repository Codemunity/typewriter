package templaters

import java.nio.file.Paths

import de.zalando.beard.renderer._
import models.Post

import scala.concurrent.{ExecutionContext, Future}

class PostTemplater(val templatePath: String) {

  val path = Paths.get(templatePath)
  val workingDirectory: String = path.getParent.toString
  val templateName: String = path.getFileName.toString

  val loader = new FileTemplateLoader(
    directoryPath = workingDirectory,
    templateSuffix = ".beard"
  )

  val templateCompiler = new CustomizableTemplateCompiler(templateLoader = loader)
  val renderer = new BeardTemplateRenderer(templateCompiler)

  def createPostTemplate(post: Post)(implicit ec: ExecutionContext): Future[String] = {
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