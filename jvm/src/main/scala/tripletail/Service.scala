package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object Service {
  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val signUp = path("sign-up") {
    post {
      entity(as[SignUp]) { signUp =>
        onSuccess(Repository.signUp(signUp.email)) { licensee =>
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signIn = path("sign-in") {
    post {
      entity(as[SignIn]) { signIn =>
        onSuccess(Repository.signIn(signIn.license, signIn.email)) {
          case Some(licensee) =>
            onSuccess(Repository.listPools(licensee.license)) { pools =>
              complete(StatusCodes.OK -> SignedIn(licensee, pools))
            }
          case None => complete(StatusCodes.Unauthorized)
        }
      }
    }
  }
  val listPools = path("list-pools") {
    post {
      entity(as[ListPools]) { listPools =>
        onSuccess(Repository.listPools(listPools.license)) { pools =>
          complete(StatusCodes.OK -> Pools(pools))
        }
      }
    }
  }
  val addPool = path("add-pool") {
    post {
      entity(as[AddPool]) { addPool =>
        onSuccess(Repository.addPool(addPool.pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val updatePool = path("update-pool") {
    post {
      entity(as[UpdatePool]) { updatePool =>
        onSuccess(Repository.updatePool(updatePool.pool)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val listSurfaces = path("list-surfaces") {
    post {
      entity(as[ListSurfaces]) { listSurfaces =>
        onSuccess(Repository.listSurfaces(listSurfaces.poolId)) { surfaces =>
          complete(StatusCodes.OK -> Surfaces(surfaces))
        }
      }
    }
  }
  val addSurface = path("add-surface") {
    post {
      entity(as[AddSurface]) { addSurface =>
        onSuccess(Repository.addSurface(addSurface.surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  }
  val updateSurface = path("update-surface") {
    post {
      entity(as[UpdateSurface]) { updateSurface =>
        onSuccess(Repository.updateSurface(updateSurface.surface)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signUp ~ signIn ~ listPools ~ addPool ~ updatePool ~ listSurfaces ~ addSurface ~ updateSurface
  }
  val routes = index ~ resources ~ api
}