import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}


object CLI extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val cmd = args.headOption.getOrElse("watch")

  val dir =
    if (args.isEmpty) "/Users/mlopezva/Desktop/codemunity"
    else args.tail.headOption.getOrElse("/Users/mlopezva/Desktop/codemunity")

  val typewriter = new Typewriter(dir)

  val f = cmd match {
    case "watch" => typewriter.run()
    case "build" => typewriter.make
    case "help" => Future.successful("Help.")
    case _ => Future.successful("Unsupported command, please run the 'help' command.")
  }

  Await.result(f, Duration.Inf)
  println(f)
}