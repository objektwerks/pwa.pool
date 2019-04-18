package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object PoolRoutes {
  def apply(poolStore: PoolStore, licenseeCache: LicenseeCache): PoolRoutes = new PoolRoutes(poolStore, licenseeCache)
}

class PoolRoutes(poolStore: PoolStore, licenseeCache: LicenseeCache) {
  import poolStore._
  import licenseeCache._

  val index = path("") {
    getFromResource("index.html")
  }
  val resources = get {
    getFromResourceDirectory("./")
  }
  val fault = path("fault") {
    post {
      entity(as[Fault]) { fault =>
        onFault(fault)
        complete(StatusCodes.OK)
      }
    }
  }
  val signup = path("signup") {
    post {
      entity(as[SignUp]) { signup =>
        onSuccess(signUp(signup.email)) { licensee =>
          cacheLicensee(licensee)
          complete(StatusCodes.OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signin =>
        onSuccess(signIn(signin.license, signin.email)) {
          case Some(licensee) =>
            cacheLicensee(licensee)
            onSuccess(listPools(licensee.license)) { pools =>
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
        onSuccess(listPools(licensee.license)) { pools =>
          complete(StatusCodes.OK -> Sequence(pools))
        }
      }
    }
  } ~ path("pools" / "add") {
    post {
      entity(as[Pool]) { pool =>
        onSuccess(addPool(pool)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pools" / "update") {
    post {
      entity(as[Pool]) { pool =>
        onSuccess(updatePool(pool)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listSurfaces(poolId.id)) { surfaces =>
          complete(StatusCodes.OK -> Sequence(surfaces))
        }
      }
    }
  } ~ path("surfaces" / "add") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(addSurface(surface)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("surfaces" / "update") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(updateSurface(surface)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listPumps(poolId.id)) { pumps =>
          complete(StatusCodes.OK -> Sequence(pumps))
        }
      }
    }
  } ~ path("pumps" / "add") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(addPump(pump)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("pumps" / "update") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(updatePump(pump)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listTimers(poolId.id)) { timers =>
          complete(StatusCodes.OK -> Sequence(timers))
        }
      }
    }
  } ~ path("timers" / "add") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(addTimer(timer)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timers" / "update") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(updateTimer(timer)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        onSuccess(listTimerSettings(timerId.id)) { timersettings =>
          complete(StatusCodes.OK -> Sequence(timersettings))
        }
      }
    }
  } ~ path("timersettings" / "add") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(addTimerSetting(timersetting)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("timersettings" / "update") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(updateTimerSetting(timersetting)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listHeaters(poolId.id)) { heaters =>
          complete(StatusCodes.OK -> Sequence(heaters))
        }
      }
    }
  } ~ path("heaters" / "add") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(addHeater(heater)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaters" / "update") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(updateHeater(heater)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val heaterons = path("heaterons") {
    post {
      entity(as[HeaterId]) { heaterId =>
        onSuccess(listHeaterOns(heaterId.id)) { heaterOns =>
          complete(StatusCodes.OK -> Sequence(heaterOns))
        }
      }
    }
  } ~ path("heaterons" / "add") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(addHeaterOn(heateron)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heaterons" / "update") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(updateHeaterOn(heateron)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val heateroffs = path("heateroffs") {
    post {
      entity(as[HeaterId]) { heaterId =>
        onSuccess(listHeaterOffs(heaterId.id)) { heaterOffs =>
          complete(StatusCodes.OK -> Sequence(heaterOffs))
        }
      }
    }
  } ~ path("heateroffs" / "add") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(addHeaterOff(heateroff)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("heateroffs" / "update") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(updateHeaterOff(heateroff)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listCleanings(poolId.id)) { cleanings =>
          complete(StatusCodes.OK -> Sequence(cleanings))
        }
      }
    }
  } ~ path("cleanings" / "add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(addCleaning(cleaning)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("cleanings" / "update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(updateCleaning(cleaning)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listMeasurements(poolId.id)) { measurements =>
          complete(StatusCodes.OK -> Sequence(measurements))
        }
      }
    }
  } ~ path("measurements" / "add") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(addMeasurement(measurement)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("measurements" / "update") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(updateMeasurement(measurement)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listChemicals(poolId.id)) { chemicals =>
          complete(StatusCodes.OK -> Sequence(chemicals))
        }
      }
    }
  } ~ path("chemicals" / "add") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(addChemical(chemical)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("chemicals" / "update") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(updateChemical(chemical)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listSupplies(poolId.id)) { supplies =>
          complete(StatusCodes.OK -> Sequence(supplies))
        }
      }
    }
  } ~ path("supplies" / "add") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(addSupply(supply)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("supplies" / "update") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(updateSupply(supply)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listRepairs(poolId.id)) { repairs =>
          complete(StatusCodes.OK -> Sequence(repairs))
        }
      }
    }
  } ~ path("repairs" / "add") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(addRepair(repair)) { id =>
          complete(StatusCodes.OK -> Id(id))
        }
      }
    }
  } ~ path("repairs" / "update") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(updateRepair(repair)) { count =>
          complete(StatusCodes.OK -> Count(count))
        }
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
      pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heaterons ~
        heateroffs ~ cleanings ~ measurements ~ chemicals ~ supplies ~ repairs
  }
  val secure = (route: Route) => headerValueByName("license") { license =>
    onSuccess(isLicenseValid(license)) { isValid =>
      if (isValid) route else complete(StatusCodes.Unauthorized)
    }
  }
  val secureApi = secure { api }
  val routes = index ~ resources ~ fault ~ signup ~ signin ~ secureApi
}