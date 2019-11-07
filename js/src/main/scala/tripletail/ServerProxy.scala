package tripletail

import org.scalajs.dom.console
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object ServerProxy {
  def apply(): ServerProxy = new ServerProxy()
}

class ServerProxy() {
  import Serializers._
  import upickle.default._

  def post(url: String, license: String, command: Command): Future[Either[Fault, Event]] = {
    val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json", Licensee.headerLicenseKey -> license)
    Ajax.post(url = url, headers = headers, data = write[Command](command)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[Event](xhr.responseText)).fold(error => Left(log(error)), event => Right(event))
        case 400 | 401 | 500 => Try(read[Fault](xhr.responseText)).fold(error => Left(log(error)), fault => Left(fault))
        case _ => Left( log(Fault(xhr.statusText, xhr.status)) )
      }
    }.recover { case error => Left( log(Fault(cause = error.getMessage)) ) }
  }

  def post(url: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json", Licensee.headerLicenseKey -> license)
    Ajax.post(url = url, headers = headers, data = write(entity)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[State](xhr.responseText)).fold(error => Left(log(error)), state => Right(state))
        case 400 | 401 | 500 => Try(read[Fault](xhr.responseText)).fold(error => Left(log(error)), fault => Left(fault))
        case _ => Left( log(Fault(xhr.statusText, xhr.status)) )
      }
    }.recover { case error => Left( log(Fault(cause = error.getMessage)) ) }
  }

  def log(error: Throwable): Fault = log(Fault(error.getMessage))

  def log(fault: Fault): Fault = {
    console.error(fault.toString)
    fault
  }
}