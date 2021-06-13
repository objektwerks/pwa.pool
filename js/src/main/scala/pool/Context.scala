package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

case class Context(urls: Urls, model: Model) extends Product with Serializable {
  val commands = Observer[Command] {
    case signup: SignUp => post(urls.signup, "", signup)
    case signin: SignIn => post(urls.signin, "", signin)
    case deactivate: DeactivateLicensee => post(urls.deactivate, deactivate.license, deactivate)
    case reactivate: ReactivateLicensee => post(urls.reactivate, reactivate.license, reactivate)
  }

  private def post(url: String,
                   license: String,
                   command: Command): Unit = {
    println(s"command > post url: $url license: $license command: $command")
    ServerProxy.post(url, license, command).map { either => resolve(either) }
    ()
  }

  private def resolve(either: Either[Fault, Event]): Unit = either.fold( fault => onFault(fault), event => onEvent(event) )

  private def onEvent(event: Event): Unit = event match {
    case signedup: SignedUp => println(s"signedup $signedup")
    case signedin: SignedIn => println(s"signedin $signedin")
    case deactivated: LicenseeDeactivated => println(s"licensee deactivated $deactivated")
    case reactivated: LicenseeReactivated => println(s"licensee reactivated $reactivated")
  }

  private def onFault(fault: Fault): Unit = println(s"fault: $fault")
}