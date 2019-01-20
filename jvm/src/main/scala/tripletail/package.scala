import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route

package object tripletail {
  def secure(route: Route): Route = headerValueByName("license") { license =>
    onSuccess(LicenseeCache.isValid(license)) { isValid =>
      if (isValid) route else complete(StatusCodes.Unauthorized)
    }
  }
}