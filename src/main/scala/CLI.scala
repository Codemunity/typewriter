import scala.concurrent.duration._
import scala.concurrent.{Await, Future}


object CLI extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val cmd = args.headOption.getOrElse("build")

  val dir =
    if (args.isEmpty) "/Users/mlopezva/Desktop/codemunity"
    else args.tail.headOption.getOrElse("/Users/mlopezva/Desktop/codemunity")

  val typewriter = new Typewriter(dir)

  val f = cmd match {
    case "watch" => typewriter.run()
    case "build" => typewriter.make.map(_ => System.exit(0))
    case "help" => Future.successful("Help.")
    case _ => Future.successful("Unsupported command, please run the 'help' command.")
  }

  val res = Await.result(f, Duration.Inf)
  println(res)

}