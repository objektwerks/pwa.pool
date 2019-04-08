package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object PoolRoutes {
  val index = path("") {
    getFromResource("index.html")
  }
  val resources = get {
    getFromResourceDirectory("./")
  }
  val signup = path("signup") {
    post {
      entity(as[SignUp]) { signUp =>
        onSuccess(PoolStore.signUp(signUp.email)) { licensee =>
          LicenseeCache.put(licensee)
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signIn =>
        onSuccess(PoolStore.signIn(signIn.license, signIn.email)) {
          case Some(licensee) =>
            LicenseeCache.put(licensee)
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
      entity(as[Pool]) { pool =>
        onSuccess(PoolStore.addPool(pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pools" / "update") {
    post {
      entity(as[Pool]) { pool =>
        onSuccess(PoolStore.updatePool(pool)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listSurfaces(poolId.id)) { surfaces =>
          complete(StatusCodes.OK -> Sequence(surfaces))
        }
      }
    }
  } ~ path("surfaces" / "add") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(PoolStore.addSurface(surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("surfaces" / "update") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(PoolStore.updateSurface(surface)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listPumps(poolId.id)) { pumps =>
          complete(StatusCodes.OK -> Sequence(pumps))
        }
      }
    }
  } ~ path("pumps" / "add") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(PoolStore.addPump(pump)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pumps" / "update") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(PoolStore.updatePump(pump)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listTimers(poolId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("timers" / "add") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(PoolStore.addTimer(timer)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timers" / "update") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(PoolStore.updateTimer(timer)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[Id]) { timerId =>
        onSuccess(PoolStore.listTimerSettings(timerId.id)) { timersettings =>
          complete(StatusCodes.OK -> Sequence(timersettings))
        }
      }
    }
  } ~ path("timersettings" / "add") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(PoolStore.addTimerSetting(timersetting)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timersettings" / "update") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(PoolStore.updateTimerSetting(timersetting)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listHeaters(poolId.id)) { heaters =>
          complete(StatusCodes.OK -> Sequence(heaters))
        }
      }
    }
  } ~ path("heaters" / "add") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(PoolStore.addHeater(heater)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaters" / "update") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(PoolStore.updateHeater(heater)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val heaterons = path("heaterons") {
    post {
      entity(as[Id]) { heaterId =>
        onSuccess(PoolStore.listHeaterOns(heaterId.id)) { heaterOns =>
          complete(StatusCodes.OK -> Sequence(heaterOns))
        }
      }
    }
  } ~ path("heaterons" / "add") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(PoolStore.addHeaterOn(heateron)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaterons" / "update") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(PoolStore.updateHeaterOn(heateron)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val heateroffs = path("heateroffs") {
    post {
      entity(as[Id]) { heaterId =>
        onSuccess(PoolStore.listHeaterOffs(heaterId.id)) { heaterOffs =>
          complete(StatusCodes.OK -> Sequence(heaterOffs))
        }
      }
    }
  } ~ path("heateroffs" / "add") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(PoolStore.addHeaterOff(heateroff)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heateroffs" / "update") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(PoolStore.updateHeaterOff(heateroff)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listCleanings(poolId.id)) { cleanings =>
          complete(StatusCodes.OK -> Sequence(cleanings))
        }
      }
    }
  } ~ path("cleanings" / "add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(PoolStore.addCleaning(cleaning)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("cleanings" / "update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(PoolStore.updateCleaning(cleaning)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listMeasurements(poolId.id)) { measurements =>
          complete(StatusCodes.OK -> Sequence(measurements))
        }
      }
    }
  } ~ path("measurements" / "add") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(PoolStore.addMeasurement(measurement)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("measurements" / "update") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(PoolStore.updateMeasurement(measurement)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listChemicals(poolId.id)) { chemicals =>
          complete(StatusCodes.OK -> Sequence(chemicals))
        }
      }
    }
  } ~ path("chemicals" / "add") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(PoolStore.addChemical(chemical)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("chemicals" / "update") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(PoolStore.updateChemical(chemical)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listSupplies(poolId.id)) { supplies =>
          complete(StatusCodes.OK -> Sequence(supplies))
        }
      }
    }
  } ~ path("supplies" / "add") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(PoolStore.addSupply(supply)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("supplies" / "update") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(PoolStore.updateSupply(supply)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[Id]) { poolId =>
        onSuccess(PoolStore.listRepairs(poolId.id)) { repairs =>
          complete(StatusCodes.OK -> Sequence(repairs))
        }
      }
    }
  } ~ path("repairs" / "add") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(PoolStore.addRepair(repair)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("repairs" / "update") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(PoolStore.updateRepair(repair)) {
          complete(StatusCodes.OK -> Ok)
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
      pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heaterons ~
        heateroffs ~ cleanings ~ measurements ~ chemicals ~ supplies ~ repairs
  }
  val secureApi = secure { api }
  val routes = index ~ resources ~ signup ~ signin ~ secureApi
}