package pool

import com.raquo.laminar.api.L._
import org.scalajs.dom.document

import scala.concurrent.ExecutionContext.Implicits.global

case class Context(urls: Urls, model: Model) extends Product with Serializable {
  val commands = Observer[Command] {
    case signup: SignUp => post(urls.signup, Licensee.emptyLicense, signup)
    case signin: SignIn => post(urls.signin, Licensee.emptyLicense, signin)
    case deactivate: DeactivateLicensee => post(urls.deactivate, deactivate.license, deactivate)
    case reactivate: ReactivateLicensee => post(urls.reactivate, reactivate.license, reactivate)
  }

  def displayToBlock(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def displayToNone(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")

  private def post(url: String,
                   license: String,
                   command: Command): Unit = {
    println(s"command > post url: $url license: $license command: $command")
    ServerProxy.post(url, license, command).map { either =>
      either.fold( fault => onFault(fault), event => onEvent(event) )
    }
    ()
  }

  private def onEvent(event: Event): Unit = event match {
    case signedup: SignedUp =>
      println(s"signedup $signedup")
      model.licensee.set( Some(signedup.licensee) )
    case signedin: SignedIn =>
      println(s"signedin $signedin")
      model.licensee.set( Some(signedin.licensee) )
    case deactivated: LicenseeDeactivated =>
      println(s"licensee deactivated $deactivated")
      model.licensee.set( Some(deactivated.licensee) )
    case reactivated: LicenseeReactivated =>
      println(s"licensee reactivated $reactivated")
      model.licensee.set( Some(reactivated.licensee) )
  }

  private def onFault(fault: Fault): Unit = println(s"fault: $fault")
}