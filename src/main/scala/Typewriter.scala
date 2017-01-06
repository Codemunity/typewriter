import scala.concurrent.{ExecutionContext, Future}


object Typewriter {

  val buildDir = "build"

  def clean()(implicit ec: ExecutionContext): Future[Unit] = {
    Future()
  }

  def build(directory: String)(implicit ec: ExecutionContext): Future[Unit] = {
    Future()
  }

  def make(directory: String) (implicit ec: ExecutionContext): Future[Unit] = {
    Future()
  }

}