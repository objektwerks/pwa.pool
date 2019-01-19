package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object PoolRoutes {
  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val signup = path("signup") {
    post {
      entity(as[Signup]) { signup =>
        onSuccess(PoolStore.signup(signup.email)) { licensee =>
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[Signin]) { signin =>
        onSuccess(PoolStore.signin(signin.license, signin.email)) {
          case Some(licensee) =>
            onSuccess(PoolStore.listPools(licensee.license)) { pools =>
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
        onSuccess(PoolStore.listPools(licensee.license)) { pools =>
          complete(StatusCodes.OK -> Sequence(pools))
        }
      }
    }
  } ~ path("pools" / "add") {
    post {
      entity(as[Item[Pool]]) { add =>
        onSuccess(PoolStore.addPool(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pools" / "update") {
    post {
      entity(as[Item[Pool]]) { update =>
        onSuccess(PoolStore.updatePool(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listSurfaces(poolId.id)) { surfaces =>
          complete(StatusCodes.OK -> Sequence(surfaces))
        }
      }
    }
  } ~ path("surfaces" / "add") {
    post {
      entity(as[Item[Surface]]) { add =>
        onSuccess(PoolStore.addSurface(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("surfaces" / "update") {
    post {
      entity(as[Item[Surface]]) { update =>
        onSuccess(PoolStore.updateSurface(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listPumps(poolId.id)) { pumps =>
          complete(StatusCodes.OK -> Sequence(pumps))
        }
      }
    }
  } ~ path("pumps" / "add") {
    post {
      entity(as[Item[Pump]]) { add =>
        onSuccess(PoolStore.addPump(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pumps" / "update") {
    post {
      entity(as[Item[Pump]]) { update =>
        onSuccess(PoolStore.updatePump(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listTimers(poolId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("timers" / "add") {
    post {
      entity(as[Item[Timer]]) { add =>
        onSuccess(PoolStore.addTimer(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timers" / "update") {
    post {
      entity(as[Item[Timer]]) { update =>
        onSuccess(PoolStore.updateTimer(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[IdLicense]) { timerId =>
        onSuccess(PoolStore.listTimerSettings(timerId.id)) { timersettings =>
          complete(StatusCodes.OK -> Sequence(timersettings))
        }
      }
    }
  } ~ path("timersettings" / "add") {
    post {
      entity(as[Item[TimerSetting]]) { add =>
        onSuccess(PoolStore.addTimerSetting(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timersettings" / "update") {
    post {
      entity(as[Item[TimerSetting]]) { update =>
        onSuccess(PoolStore.updateTimerSetting(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listHeaters(poolId.id)) { heaters =>
          complete(StatusCodes.OK -> Sequence(heaters))
        }
      }
    }
  } ~ path("heaters" / "add") {
    post {
      entity(as[Item[Heater]]) { add =>
        onSuccess(PoolStore.addHeater(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaters" / "update") {
    post {
      entity(as[Item[Heater]]) { update =>
        onSuccess(PoolStore.updateHeater(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val heaterons = path("heaterons") {
    post {
      entity(as[IdLicense]) { heaterId =>
        onSuccess(PoolStore.listHeaterOns(heaterId.id)) { heaterOns =>
          complete(StatusCodes.OK -> Sequence(heaterOns))
        }
      }
    }
  } ~ path("heaterons" / "add") {
    post {
      entity(as[Item[HeaterOn]]) { add =>
        onSuccess(PoolStore.addHeaterOn(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaterons" / "update") {
    post {
      entity(as[Item[HeaterOn]]) { update =>
        onSuccess(PoolStore.updateHeaterOn(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val heateroffs = path("heateroffs") {
    post {
      entity(as[IdLicense]) { heaterId =>
        onSuccess(PoolStore.listHeaterOffs(heaterId.id)) { heaterOffs =>
          complete(StatusCodes.OK -> Sequence(heaterOffs))
        }
      }
    }
  } ~ path("heateroffs" / "add") {
    post {
      entity(as[Item[HeaterOff]]) { add =>
        onSuccess(PoolStore.addHeaterOff(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heateroffs" / "update") {
    post {
      entity(as[Item[HeaterOff]]) { update =>
        onSuccess(PoolStore.updateHeaterOff(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listCleanings(poolId.id)) { cleanings =>
          complete(StatusCodes.OK -> Sequence(cleanings))
        }
      }
    }
  } ~ path("cleanings" / "add") {
    post {
      entity(as[Item[Cleaning]]) { add =>
        onSuccess(PoolStore.addCleaning(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("cleanings" / "update") {
    post {
      entity(as[Item[Cleaning]]) { update =>
        onSuccess(PoolStore.updateCleaning(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listMeasurements(poolId.id)) { measurements =>
          complete(StatusCodes.OK -> Sequence(measurements))
        }
      }
    }
  } ~ path("measurements" / "add") {
    post {
      entity(as[Item[Measurement]]) { add =>
        onSuccess(PoolStore.addMeasurement(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("measurements" / "update") {
    post {
      entity(as[Item[Measurement]]) { update =>
        onSuccess(PoolStore.updateMeasurement(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listChemicals(poolId.id)) { chemicals =>
          complete(StatusCodes.OK -> Sequence(chemicals))
        }
      }
    }
  } ~ path("chemicals" / "add") {
    post {
      entity(as[Item[Chemical]]) { add =>
        onSuccess(PoolStore.addChemical(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("chemicals" / "update") {
    post {
      entity(as[Item[Chemical]]) { update =>
        onSuccess(PoolStore.updateChemical(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listSupplies(poolId.id)) { supplies =>
          complete(StatusCodes.OK -> Sequence(supplies))
        }
      }
    }
  } ~ path("supplies" / "add") {
    post {
      entity(as[Item[Supply]]) { add =>
        onSuccess(PoolStore.addSupply(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("supplies" / "update") {
    post {
      entity(as[Item[Supply]]) { update =>
        onSuccess(PoolStore.updateSupply(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[IdLicense]) { poolId =>
        onSuccess(PoolStore.listRepairs(poolId.id)) { repairs =>
          complete(StatusCodes.OK -> Sequence(repairs))
        }
      }
    }
  } ~ path("repairs" / "add") {
    post {
      entity(as[Item[Repair]]) { add =>
        onSuccess(PoolStore.addRepair(add.item)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("repairs" / "update") {
    post {
      entity(as[Item[Repair]]) { update =>
        onSuccess(PoolStore.updateRepair(update.item)) {
          complete(StatusCodes.OK)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heaterons ~
      heateroffs ~ cleanings ~ measurements ~ chemicals ~ supplies ~ repairs
  }
  val routes = index ~ resources ~ signup ~ signin ~ api
}