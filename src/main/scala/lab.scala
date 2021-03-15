import akka.actor._

object Pinger {
  case class Pong()
}

// object for each actor
object Ponger {
  // expected message
  case class Ping()
}

object Printer{
  case class message(text :String)
}


class Pinger extends Actor {
  import Pinger._

  var countDown = 100

  def receive: PartialFunction [Any, Unit] = {

    case Pong =>
      val printer = context.actorOf(Props[Printer]) //context refers to system below
      printer ! Printer.message(s"received pong, count down $countDown")
      printer ! PoisonPill
      if (countDown > 0) {
        countDown -= 1
        sender() ! Ponger.Ping
      } else {
        sender() ! PoisonPill
        context.stop(self)
      }
  }
}



class Ponger (pinger: ActorRef) extends Actor {
  import Ponger._

  def receive: PartialFunction [Any, Unit] = {

    case Ping =>
      val printer = context.actorOf(Props[Printer])
      printer ! Printer.message("received ping")
      printer ! PoisonPill
      pinger ! Pinger.Pong
  }
}



class Printer extends Actor {
  import Printer._
  def receive: PartialFunction [Any, Unit] = {

    case message(text) =>
      println(s"${sender().path}: $text")

  }
}

object PingPong extends App {

  val system = ActorSystem("pingpong")

  val pinger = system.actorOf(Props[Pinger], "pinger")

  val ponger = system.actorOf(Props(classOf[Ponger], pinger), "ponger")

  ponger ! Ponger.Ping
}