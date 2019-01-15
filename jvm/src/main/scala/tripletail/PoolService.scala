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
      entity(as[SignUp]) { signUp =>
        onSuccess(PoolRepository.signUp(signUp.email)) { licensee =>
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signIn =>
        onSuccess(PoolRepository.signIn(signIn.license, signIn.email)) {
          case Some(licensee) =>
            onSuccess(PoolRepository.getPools(licensee.license)) { pools =>
              complete(StatusCodes.OK -> SignedIn(licensee, pools))
            }
          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[PostPool]) { postPool =>
        onSuccess(PoolRepository.postPool(postPool.pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signup ~ signin ~ pools
  }
  val routes = index ~ resources ~ api
}