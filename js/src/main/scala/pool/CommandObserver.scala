package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object CommandObserver {
  def apply(apiUrl: String): Observer[Command] = Observer[Command] {
    case signup: SignUp => post(s"$apiUrl/signup", "", signup)
    case signin: SignIn => post(s"$apiUrl/signin", "", signin)
    case deactivate: DeactivateLicensee => post(s"$apiUrl/deactivatelicensee", deactivate.license, deactivate)
    case reactivate: ReactivateLicensee => post(s"$apiUrl/reactivatelicensee", reactivate.license, reactivate)
  }

  def post(url: String,
           license: String,
           command: Command): Unit = {
    println(s"url: $url license:$license command: $command")
    ServerProxy.post(url, license, command).map {
      case Right(event) => EventHandler.handle(event)
      case Left(fault) => println(s"$url : $fault")
    }
    ()
  }
}