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
            onSuccess(PoolRepository.listPools(licensee.license)) { pools =>
              complete(StatusCodes.OK -> SignedIn(licensee, pools))
            }
          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
  val addPool = path("addPool") {
    post {
      entity(as[AddPool]) { addPool =>
        onSuccess(PoolRepository.addPool(addPool.pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val surfaces = path("listSurfaces") {
    post {
      entity(as[ListSurfaces]) { listSurfaces =>
        onSuccess(PoolRepository.listSurfaces(listSurfaces.poolId)) { surfaces =>
          complete(StatusCodes.OK -> Surfaces(surfaces))
        }
      }
    }
  }
  val addSurface = path("addSurface") {
    post {
      entity(as[AddSurface]) { addSurface =>
        onSuccess(PoolRepository.addSurface(addSurface.surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signup ~ signin ~ addPool ~ surfaces ~ addSurface
  }
  val routes = index ~ resources ~ api
}