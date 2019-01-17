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
      entity(as[Licensee]) { licensee =>
        onSuccess(Repository.listPools(licensee.license)) { pools =>
          complete(StatusCodes.OK -> Sequence(pools))
        }
      }
    }
  } ~ path("pools" / "add") {
    post {
      entity(as[Item[Pool]]) { add =>
        onSuccess(Repository.addPool(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pools" / "update") {
    post {
      entity(as[Item[Pool]]) { update =>
        onSuccess(Repository.updatePool(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(Repository.listSurfaces(poolId.id)) { surfaces =>
          complete(StatusCodes.OK -> Sequence(surfaces))
        }
      }
    }
  } ~ path("surfaces" / "add") {
    post {
      entity(as[Item[Surface]]) { add =>
        onSuccess(Repository.addSurface(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("surfaces" / "update") {
    post {
      entity(as[Item[Surface]]) { update =>
        onSuccess(Repository.updateSurface(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(Repository.listPumps(poolId.id)) { pumps =>
          complete(StatusCodes.OK -> Sequence(pumps))
        }
      }
    }
  } ~ path("pumps" / "add") {
    post {
      entity(as[Item[Pump]]) { add =>
        onSuccess(Repository.addPump(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pumps" / "update") {
    post {
      entity(as[Item[Pump]]) { update =>
        onSuccess(Repository.updatePump(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(Repository.listTimers(poolId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("timers" / "add") {
    post {
      entity(as[Item[Timer]]) { add =>
        onSuccess(Repository.addTimer(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timers" / "update") {
    post {
      entity(as[Item[Timer]]) { update =>
        onSuccess(Repository.updateTimer(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[IdLicense]) { timerId =>
        onSuccess(Repository.listTimerSettings(timerId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("timersettings" / "add") {
    post {
      entity(as[Item[TimerSetting]]) { add =>
        onSuccess(Repository.addTimerSetting(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timersettings" / "update") {
    post {
      entity(as[Item[TimerSetting]]) { update =>
        onSuccess(Repository.updateTimerSetting(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(Repository.listHeaters(poolId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("heaters" / "add") {
    post {
      entity(as[Item[Heater]]) { add =>
        onSuccess(Repository.addHeater(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaters" / "update") {
    post {
      entity(as[Item[Heater]]) { update =>
        onSuccess(Repository.updateHeater(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signUp ~ signIn ~ pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters
  }
  val routes = index ~ resources ~ api
}