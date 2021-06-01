package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object CommandObserver {
  def apply(apiUrl: String,
            serverProxy: ServerProxy,
            eventHandler: EventHandler): Observer[Command] = Observer[Command] {
    case signup: SignUp =>
      val url = s"$apiUrl/signup"
      println(s"$url : $signup")
      post(serverProxy, url, "", signup, eventHandler)
    case signin: SignIn =>
      val url = s"$apiUrl/signin"
      println(s"$url : $signin")
      post(serverProxy, url, "", signin, eventHandler)
    case deactivate: DeactivateLicensee =>
      val url = s"$apiUrl/deactivatelicensee"
      println(s"$url : $deactivate")
      post(serverProxy, url, deactivate.license, deactivate, eventHandler)
    case reactivate: ReactivateLicensee =>
      val url = s"$apiUrl/reactivatelicensee"
      println(s"$url : $reactivate")
      post(serverProxy, url, reactivate.license, reactivate, eventHandler)
  }

  def post(serverProxy: ServerProxy,
           url: String,
           license: String,
           command: Command,
           eventHandler: EventHandler): Unit = {
    serverProxy.post(url, license, command).map {
      case Right(event) => eventHandler.handle(event)
      case Left(fault) => println(s"$url : $fault")
    }
    ()
  }
}