package tripletail

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.console

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PoolServerClient(serverUrl: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def post(path: String, command: Command): Future[Either[Fault, Event]] = {
    Ajax.post(url = serverUrl + path, headers = headers, data = command.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[Event](xhr.responseText).fold(e => Left(Fault(e)), v => Right(v))
        case _ => Left(Fault(xhr.status))
      }
    }.recover { case e => Left(Fault(e)) }
  }

  def post(path: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    val headersWithLicense = headers ++: Map("license" -> license)
    Ajax.post(url = serverUrl + path, headers = headersWithLicense, data = entity.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[State](xhr.responseText).fold(e => Left(Fault(e)), v => Right(v))
        case _ => Left(Fault(xhr.status))
      }
    }.recover { case e => Left(Fault(e)) }
  }

  def post(path: String, fault: Fault): Unit = {
    Ajax.post(url = serverUrl + path, headers = headers, data = fault.asJson.toString).map { _ =>
      console.error(fault.toString)
    }
    ()
  }
}

object PoolServerClient {
  def apply(serverUrl: String): PoolServerClient = new PoolServerClient(serverUrl)
}