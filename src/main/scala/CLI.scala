

import scala.concurrent.duration.Duration
import scala.concurrent.Await


object CLI extends App {

  import scala.concurrent.ExecutionContext.Implicits.global

  val dir = args.headOption.getOrElse("/Users/mlopezva/Desktop/codemunity")

  val typewriter = new Typewriter(dir)
  val f = typewriter.run()

  Await.result(f, Duration.Inf)
  println(f)
}