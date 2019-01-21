package tripletail

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._
import org.scalajs.dom.ext.Ajax

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

class PoolRestClient(url: String) {
  val headers = Map("Content-Type" -> "application/json; charset=utf-8", "Accept" -> "application/json")

  def signup(signup: SignUp): Future[Either[Fault, SignedUp]] = {
    Ajax.post(url = url, headers = headers, data = signup.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[SignedUp](xhr.responseText).fold(e => Left(Fault(e.getMessage)), v => Right(v))
        case _ => Left(toFault(xhr.status))
      }
    }
  }

  def signin(signin: SignIn): Future[Either[Fault, SignedIn]] = {
    Ajax.post(url = url, headers = headers, data = signin.asJson.toString).map { xhr =>
      xhr.status match {
        case 200 => decode[SignedIn](xhr.responseText).fold(e => Left(Fault(e.getMessage)), v => Right(v))
        case _ => Left(toFault(xhr.status))
      }
    }
  }

  def toFault(statusCode: Int): Fault = Fault(s"status code: ${statusCode.toString}")
}

object PoolRestClient {
  def apply(url: String): PoolRestClient = new PoolRestClient(url)
}