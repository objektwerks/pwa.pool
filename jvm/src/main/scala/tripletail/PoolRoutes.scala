package tripletail

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import de.heikoseeberger.akkahttpupickle.UpickleSupport._
import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

object PoolRoutes {
  def apply(poolStore: PoolStore, licenseeCache: LicenseeCache): PoolRoutes = new PoolRoutes(poolStore, licenseeCache)
}

class PoolRoutes(poolStore: PoolStore, licenseeCache: LicenseeCache) {
  import Serializers._
  import StatusCodes._
  import licenseeCache._
  import poolStore._

  val logger = LoggerFactory.getLogger(PoolRoutes.getClass.getSimpleName)

  implicit val internalExceptionHandler = ExceptionHandler {
    case NonFatal(error) =>
      extractRequestContext { context =>
        logger.error(s"*** Handling ${context.request.uri} failed: ${error.getMessage}", error)
        context.request.discardEntityBytes(context.materializer)
        val fault = Fault(error)
        addFault(fault)
        complete(InternalServerError -> fault)
      }
  }

  val index = path("") {
    getFromResource("index.html")
  }
  val resources = get {
    getFromResourceDirectory("./")
  }
  val signup = path("signup") {
    post {
      entity(as[Signup]) { signup =>
        onSuccess(signUp(signup.email)) { licensee =>
          cacheLicensee(licensee)
          complete(OK -> Secure(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[Signin]) { signin =>
        onSuccess(signIn(signin.license, signin.email)) {
          case Some(licensee) =>
            cacheLicensee(licensee)
            complete(OK -> Secure(licensee))
          case None =>
            val fault = Fault(message = "Invalid licensee!", code = Unauthorized.intValue)
            addFault(fault)
            complete(Unauthorized -> fault)
        }
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[Licensee]) { licensee =>
        onSuccess(listPools(licensee.license)) { pools =>
          complete(OK -> Pools(pools))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pool]) { pool =>
        onSuccess(addPool(pool)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pool]) { pool =>
        onSuccess(updatePool(pool)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listSurfaces(poolId.id)) { surfaces =>
          complete(OK -> Surfaces(surfaces))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(addSurface(surface)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Surface]) { surface =>
        onSuccess(updateSurface(surface)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listPumps(poolId.id)) { pumps =>
          complete(OK -> Pumps(pumps))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(addPump(pump)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pump]) { pump =>
        onSuccess(updatePump(pump)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listTimers(poolId.id)) { timers =>
          complete(OK -> Timers(timers))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(addTimer(timer)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Timer]) { timer =>
        onSuccess(updateTimer(timer)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        onSuccess(listTimerSettings(timerId.id)) { timersettings =>
          complete(OK -> TimerSettings(timersettings))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(addTimerSetting(timersetting)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[TimerSetting]) { timersetting =>
        onSuccess(updateTimerSetting(timersetting)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listHeaters(poolId.id)) { heaters =>
          complete(OK -> Heaters(heaters))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(addHeater(heater)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Heater]) { heater =>
        onSuccess(updateHeater(heater)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val heaterons = path("heaterons") {
    post {
      entity(as[HeaterId]) { heaterId =>
        onSuccess(listHeaterOns(heaterId.id)) { heaterOns =>
          complete(OK -> HeaterOns(heaterOns))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(addHeaterOn(heateron)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterOn]) { heateron =>
        onSuccess(updateHeaterOn(heateron)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val heateroffs = path("heateroffs") {
    post {
      entity(as[HeaterId]) { heaterId =>
        onSuccess(listHeaterOffs(heaterId.id)) { heaterOffs =>
          complete(OK -> HeaterOffs(heaterOffs))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(addHeaterOff(heateroff)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterOff]) { heateroff =>
        onSuccess(updateHeaterOff(heateroff)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listCleanings(poolId.id)) { cleanings =>
          complete(OK -> Cleanings(cleanings))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(addCleaning(cleaning)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        onSuccess(updateCleaning(cleaning)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listMeasurements(poolId.id)) { measurements =>
          complete(OK -> Measurements(measurements))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(addMeasurement(measurement)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Measurement]) { measurement =>
        onSuccess(updateMeasurement(measurement)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listChemicals(poolId.id)) { chemicals =>
          complete(OK -> Chemicals(chemicals))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(addChemical(chemical)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Chemical]) { chemical =>
        onSuccess(updateChemical(chemical)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listSupplies(poolId.id)) { supplies =>
          complete(OK -> Supplies(supplies))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(addSupply(supply)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Supply]) { supply =>
        onSuccess(updateSupply(supply)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[PoolId]) { poolId =>
        onSuccess(listRepairs(poolId.id)) { repairs =>
          complete(OK -> Repairs(repairs))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(addRepair(repair)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Repair]) { repair =>
        onSuccess(updateRepair(repair)) { count =>
          complete(OK -> Updated(count))
        }
      }
    }
  }
  val url = "/api/v1/tripletail"
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signin ~ pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heaterons ~ heateroffs ~
      cleanings ~ measurements ~ chemicals ~ supplies ~ repairs
  }
  val secure = (route: Route) => headerValueByName(Licensee.licenseHeaderKey) { license =>
    onSuccess(isLicenseValid(license)) { isValid =>
      if (isValid) route
      else {
        val fault = Fault(message = "Invalid license key!", code = Unauthorized.intValue)
        addFault(fault)
        complete(Unauthorized -> fault)
      }
    }
  }
  val secureApi = secure { api }
  val routes = Route.seal( index ~ resources ~ signup ~ secureApi )
}