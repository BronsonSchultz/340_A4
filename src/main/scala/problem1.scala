
import akka.actor._


object Printer{
  case class message(text :String)
}

object MinHolder {
  case class value(i: Int)
}

object Client {

}

class MinHolder extends Actor {
  import  MinHolder._

  var m: Int = null

  def receive: PartialFunction [Any, Unit] = {
    case value(i) =>
      if (i < m){
        m = i
      } else {
        val nextHolder = context.actorOf(Props[MinHolder])
        self ! MinHolder.value(i)
      }
  }
}