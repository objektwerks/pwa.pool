package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

case class Urls(signup: String, signin: String, deactivate: String, reactivate: String)

object Context {
  def apply(apiUrl: String): Context = {
    val urls = Urls(
      s"$apiUrl/signup",
      s"$apiUrl/signin",
      s"$apiUrl/deactivatelicensee",
      s"$apiUrl/reactivatelicensee"
    )
    new Context(urls)
  }
}

class Context(urls: Urls) {
  val commandObserver = Observer[Command] {
    case signup: SignUp => post(urls.signup, "", signup)
    case signin: SignIn => post(urls.signin, "", signin)
    case deactivate: DeactivateLicensee => post(urls.deactivate, deactivate.license, deactivate)
    case reactivate: ReactivateLicensee => post(urls.reactivate, reactivate.license, reactivate)
  }

  private def post(url: String,
                   license: String,
                   command: Command): Unit = {
    println(s"url: $url license: $license command: $command")
    ServerProxy.post(url, license, command).map { either => handle(either) }
    ()
  }

  private def handle(either: Either[Fault, Event]): Unit = either match {
    case Right(event) => handle(event)
    case Left(fault) => handle(fault)
  }

  private def handle(event: Event): Unit = event match {
    case signedup: SignedUp => println(s"signedup $signedup")
    case signedin: SignedIn => println(s"signedin $signedin")
    case deactivated: LicenseeDeactivated => println(s"licensee deactivated $deactivated")
    case reactivated: LicenseeReactivated => println(s"licensee reactivated $reactivated")
  }

  private def handle(fault: Fault): Unit = println(s"$fault")
}