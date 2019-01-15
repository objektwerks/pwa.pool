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
  val pools = path("pools") {
    post {
      entity(as[ListPools]) { listPools =>
        onSuccess(Repository.listPools(listPools.license)) { pools =>
          complete(StatusCodes.OK -> Pools(pools))
        }
      }
    }
  } ~ path("pools" / "add") {
    post {
      entity(as[SavePool]) { savePool =>
        onSuccess(Repository.addPool(savePool.pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pools" / "update") {
    post {
      entity(as[SavePool]) { savePool =>
        onSuccess(Repository.updatePool(savePool.pool)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[ListSurfaces]) { listSurfaces =>
        onSuccess(Repository.listSurfaces(listSurfaces.poolId)) { surfaces =>
          complete(StatusCodes.OK -> Surfaces(surfaces))
        }
      }
    }
  } ~ path("surfaces" / "add") {
    post {
      entity(as[SaveSurface]) { saveSurface =>
        onSuccess(Repository.addSurface(saveSurface.surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("surfaces" / "update") {
    post {
      entity(as[SaveSurface]) { saveSurface =>
        onSuccess(Repository.updateSurface(saveSurface.surface)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[ListPumps]) { listPumps =>
        onSuccess(Repository.listPumps(listPumps.poolId)) { pumps =>
          complete(StatusCodes.OK -> Pumps(pumps))
        }
      }
    }
  } ~ path("pumps" / "add") {
    post {
      entity(as[SavePump]) { savePump =>
        onSuccess(Repository.addPump(savePump.pump)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pumps" / "update") {
    post {
      entity(as[SavePump]) { savePump =>
        onSuccess(Repository.updatePump(savePump.pump)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[ListTimers]) { listTimers =>
        onSuccess(Repository.listTimers(listTimers.poolId)) { timers =>
          complete(StatusCodes.OK -> Timers(timers))
        }
      }
    }
  } ~ path("timers" / "add") {
    post {
      entity(as[SaveTimer]) { saveTimer =>
        onSuccess(Repository.addTimer(saveTimer.timer)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timers" / "update") {
    post {
      entity(as[SaveTimer]) { saveTimer =>
        onSuccess(Repository.updateTimer(saveTimer.timer)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signUp ~ signIn ~ pools ~ surfaces ~ pumps ~ timers
  }
  val routes = index ~ resources ~ api
}