package pool

import org.scalajs.dom.console
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object ServerProxy {
  import Serializers._
  import upickle.default._

  val headers = Map(
    "Content-Type" -> "application/json; charset=utf-8",
    "Accept" -> "application/json"
  )

  def headers(license: String): Map[String, String] =
    if (license.nonEmpty) headers + ( Account.headerLicenseKey -> license )
    else headers

  def post(url: String): Future[String] =
    Ajax.post(url = url, headers = headers).map { xhr =>
      xhr.status match {
        case 200 => xhr.responseText
        case _ => xhr.statusText
      }
    }.recover { case error => error.getMessage }

  def post(url: String, license: String, command: Command): Future[Either[Fault, Event]] = {
    console.debug(s"Command: $command")
    Ajax.post(url = url, headers = headers(license), data = write[Command](command)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[Event](xhr.responseText)).fold(error => Left(log(error)), event => Right(event))
        case 400 | 401 | 500 => toFault(xhr.responseText)
        case _ => Left( log(xhr.statusText, xhr.status) )
      }
    }.recover { case error => Left( log(error) ) }
  }

  def post(url: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    console.debug(s"Entity: $entity")
    Ajax.post(url = url, headers = headers(license), data = write[Entity](entity)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[State](xhr.responseText)).fold(error => Left(log(error)), state => Right(state))
        case 400 | 401 | 500 => toFault(xhr.responseText)
        case _ => Left( log(xhr.statusText, xhr.status) )
      }
    }.recover { case error => Left( log(error) ) }
  }

  def toFault(responseText: String): Left[Fault, Nothing] =
    Try(read[Fault](responseText)).fold(error => Left(log(error)), fault => Left(fault))

  def log(error: Throwable): Fault = {
    console.error(error.printStackTrace())
    log(error.getMessage)
  }

  def log(statusText: String, status: Int = 500): Fault = {
    val fault = Fault(statusText, status)
    console.error(fault.toString)
    fault
  }
}