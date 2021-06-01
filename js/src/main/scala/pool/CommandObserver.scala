package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object CommandObserver {
  def apply(apiUrl: String,
            serverProxy: ServerProxy,
            eventHandler: EventHandler): Observer[Command] = Observer[Command] {
    case signup: SignUp =>
      post(serverProxy, s"$apiUrl/signup", "", signup, eventHandler)
    case signin: SignIn =>
      post(serverProxy, s"$apiUrl/signin", "", signin, eventHandler)
    case deactivate: DeactivateLicensee =>
      post(serverProxy, s"$apiUrl/deactivatelicensee", deactivate.license, deactivate, eventHandler)
    case reactivate: ReactivateLicensee =>
      post(serverProxy, s"$apiUrl/reactivatelicensee", reactivate.license, reactivate, eventHandler)
  }

  def post(serverProxy: ServerProxy,
           url: String,
           license: String,
           command: Command,
           eventHandler: EventHandler): Unit = {
    println(s"url: $url license:$license command: $command")
    serverProxy.post(url, license, command).map {
      case Right(event) => eventHandler.handle(event)
      case Left(fault) => println(s"$url : $fault")
    }
    ()
  }
}