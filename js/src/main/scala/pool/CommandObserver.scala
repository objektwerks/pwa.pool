package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object CommandObserver {
  def apply(urls: Map[String, String]): Observer[Command] = Observer[Command] {
    case signup: SignUp => post(urls("signup"), "", signup)
    case signin: SignIn => post(urls("signin"), "", signin)
    case deactivate: DeactivateLicensee => post(urls("deactivate"), deactivate.license, deactivate)
    case reactivate: ReactivateLicensee => post(urls("reactivate"), reactivate.license, reactivate)
  }

  private def post(url: String,
                   license: String,
                   command: Command): Unit = {
    println(s"url: $url license: $license command: $command")
    ServerProxy.post(url, license, command).map { either => EventHandler.handle(either) }
    ()
  }
}