package tripletail

import java.time.LocalDate
import java.util.UUID

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object PoolService {
  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val signup = path("signup") {
    post {
      entity(as[SignUp]) { signup =>
        val licensee = Licensee(UUID.randomUUID.toString, LocalDate.now, signup.email)
        onSuccess(PoolRepository.signup(licensee)) {
          complete(StatusCodes.OK -> SignedIn(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signin =>
        onSuccess(PoolRepository.signin(signin.license)) {
          case Some(licensee) => complete(StatusCodes.OK -> SignedIn(licensee))
          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signup ~ signin
  }
  val routes = index ~ resources ~ api
}