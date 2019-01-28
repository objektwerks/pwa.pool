package tripletail

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PoolRestClient(url: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def post(path: String, command: Command): Future[Either[Fault, Event]] = {
    Ajax.post(url = url + path, headers = headers, data = command.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[Event](xhr.responseText).fold(e => Left(toFault(e)), v => Right(v))
        case _ => Left(toFault(xhr.status))
      }
    }.recover { case e => Left(toFault(e)) }
  }

  def post(path: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    val headersWithLicense = headers ++: Map("license" -> license)
    Ajax.post(url = url + path, headers = headersWithLicense, data = entity.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[State](xhr.responseText).fold(e => Left(toFault(e)), v => Right(v))
        case _ => Left(toFault(xhr.status))
      }
    }.recover { case e => Left(toFault(e)) }
  }

  def toFault(statusCode: Int): Fault = {
    println(s"post http status code error: $statusCode")
    Fault(s"http status code: ${statusCode.toString}", statusCode)
  }

  def toFault(t: Throwable): Fault = {
    println(s"post method exception: ${t.printStackTrace()}")
    Fault(s"${t.getMessage}")
  }
}

object PoolRestClient {
  def apply(url: String): PoolRestClient = new PoolRestClient(url)
}