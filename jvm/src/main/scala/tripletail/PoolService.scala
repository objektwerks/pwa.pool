package tripletail

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
        onSuccess(PoolRepository.signup(signup.email)) { licensee =>
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signin =>
        onSuccess(PoolRepository.signin(signin.license, signin.email)) {
          case Some(licensee) =>
            onSuccess(PoolRepository.pools(licensee.license)) { pools =>
              complete(StatusCodes.OK -> SignedIn(licensee, pools))
            }
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