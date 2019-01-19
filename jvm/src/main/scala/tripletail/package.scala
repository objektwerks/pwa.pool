import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import io.circe.generic.auto._
import io.circe.parser.decode

import scala.concurrent.Future

package object tripletail {
  private def isLicenseeSecure(licensee: Licensee): Future[Boolean] = Future.successful(licensee.license.nonEmpty)

  def secure(route: Route): Route = headerValueByName("licensee") { json =>
    decode[Licensee](json) match {
      case Right(licensee) => onSuccess(isLicenseeSecure(licensee)) { isSecure =>
        if (isSecure) route else complete(StatusCodes.Unauthorized)
      }
      case Left(error) => extractLog { log =>
        log.error(error, s"Licensee json parsing failed: ${error.getMessage}")
        complete(StatusCodes.Unauthorized)
      }
    }
  }
}