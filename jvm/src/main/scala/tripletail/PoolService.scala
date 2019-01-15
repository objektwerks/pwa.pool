package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import tripletail.{ PoolRepository => Pools }

object PoolService {
  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val signUp = path("sign-up") {
    post {
      entity(as[SignUp]) { signUp =>
        onSuccess(Pools.signUp(signUp.email)) { licensee =>
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signIn = path("sign-in") {
    post {
      entity(as[SignIn]) { signIn =>
        onSuccess(Pools.signIn(signIn.license, signIn.email)) {
          case Some(licensee) =>
            onSuccess(Pools.listPools(licensee.license)) { pools =>
              complete(StatusCodes.OK -> SignedIn(licensee, pools))
            }
          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
  val addPool = path("add-pool") {
    post {
      entity(as[AddPool]) { addPool =>
        onSuccess(Pools.addPool(addPool.pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val listSurfaces = path("list-surfaces") {
    post {
      entity(as[ListSurfaces]) { listSurfaces =>
        onSuccess(Pools.listSurfaces(listSurfaces.poolId)) { surfaces =>
          complete(StatusCodes.OK -> Surfaces(surfaces))
        }
      }
    }
  }
  val addSurface = path("add-surface") {
    post {
      entity(as[AddSurface]) { addSurface =>
        onSuccess(Pools.addSurface(addSurface.surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signUp ~ signIn ~ addPool ~ listSurfaces ~ addSurface
  }
  val routes = index ~ resources ~ api
}