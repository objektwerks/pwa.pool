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
  import Validators._
  import StatusCodes._
  import licenseeCache._
  import poolStore._

  val logger = LoggerFactory.getLogger(PoolRoutes.getClass.getSimpleName)

  val onUnauthorized = (message: String) => {
    logger.error(message)
    addFault(Fault(message = message, code = Unauthorized.intValue))
  }

  val onInvalid = (entity: Entity) => {
    val message = s"*** Invalid: $entity"
    logger.error(message)
    addFault(Fault(message = message, code = BadRequest.intValue))
  }

  val onFault = (message: String) => {
    logger.error(message)
    addFault(Fault(message = message))
  }

  implicit val onException = ExceptionHandler {
    case NonFatal(error) =>
      extractRequestContext { context =>
        val message = s"*** Handling ${context.request.uri} failed: ${error.getMessage}"
        context.request.discardEntityBytes(context.materializer)
        complete(InternalServerError -> onFault(message))
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
      entity(as[SignUp]) { signup =>
        if (signup.isValid) {
          onSuccess(signUp(signup.email)) { licensee =>
            cacheLicensee(licensee)
            complete(OK -> Secure(licensee))
          }
        } else complete(BadRequest -> onInvalid(signup))
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signin =>
        if (signin.isValid) {
          onSuccess(signIn(signin.license, signin.email)) {
            case Some(licensee) =>
              cacheLicensee(licensee)
              complete(OK -> Secure(licensee))
            case None =>
              val message = s"*** Signin for email: ${signin.email} and license: ${signin.license} failed!"
              complete(Unauthorized -> onUnauthorized(message))
          }
        } else complete(BadRequest -> onInvalid(signin))
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[Licensee]) { licensee =>
        if (licensee.isValid) {
          onSuccess(listPools(licensee.license)) { pools =>
            complete(OK -> Pools(pools))
          }
        } else complete(BadRequest -> onInvalid(licensee))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pool]) { pool =>
        if(pool.isValid) {
          onSuccess(addPool(pool)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(pool))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pool]) { pool =>
        if(pool.isValid) {
          onSuccess(updatePool(pool)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(pool))
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) {
          onSuccess(listSurfaces(poolId.id)) { surfaces =>
            complete(OK -> Surfaces(surfaces))
          }
        } else complete(BadRequest -> onInvalid(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) {
          onSuccess(addSurface(surface)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(surface))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) {
          onSuccess(updateSurface(surface)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(surface))
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) {
          onSuccess(listPumps(poolId.id)) { pumps =>
            complete(OK -> Pumps(pumps))
          }
        } else complete(BadRequest -> onInvalid(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) {
          onSuccess(addPump(pump)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(pump))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) {
          onSuccess(updatePump(pump)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(pump))
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) {
          onSuccess(listTimers(poolId.id)) { timers =>
            complete(OK -> Timers(timers))
          }
        } else complete(BadRequest -> onInvalid(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) {
          onSuccess(addTimer(timer)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(timer))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) {
          onSuccess(updateTimer(timer)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(timer))
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        if (timerId.isValid) {
          onSuccess(listTimerSettings(timerId.id)) { timersettings =>
            complete(OK -> TimerSettings(timersettings))
          }
        } else complete(BadRequest -> onInvalid(timerId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) {
          onSuccess(addTimerSetting(timerSetting)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(timerSetting))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) {
          onSuccess(updateTimerSetting(timerSetting)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(timerSetting))
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) {
          onSuccess(listHeaters(poolId.id)) { heaters =>
            complete(OK -> Heaters(heaters))
          }
        } else complete(BadRequest -> onInvalid(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) {
          onSuccess(addHeater(heater)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(heater))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) {
          onSuccess(updateHeater(heater)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(heater))
      }
    }
  }
  val heaterons = path("heaterons") {
    post {
      entity(as[HeaterId]) { heaterId =>
        if (heaterId.isValid) {
          onSuccess(listHeaterOns(heaterId.id)) { heaterOns =>
            complete(OK -> HeaterOns(heaterOns))
          }
        } else complete(BadRequest -> onInvalid(heaterId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterOn]) { heaterOn =>
        if (heaterOn.isValid) {
          onSuccess(addHeaterOn(heaterOn)) { id =>
            complete(OK -> Generated(id))
          }
        } else complete(BadRequest -> onInvalid(heaterOn))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterOn]) { heaterOn =>
        if (heaterOn.isValid) {
          onSuccess(updateHeaterOn(heaterOn)) { count =>
            complete(OK -> Updated(count))
          }
        } else complete(BadRequest -> onInvalid(heaterOn))
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
      entity(as[HeaterOff]) { heaterOff =>
        onSuccess(addHeaterOff(heaterOff)) { id =>
          complete(OK -> Generated(id))
        }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterOff]) { heaterOff =>
        onSuccess(updateHeaterOff(heaterOff)) { count =>
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
        val message = s"*** License [ $license ] is invalid!"
        complete(Unauthorized -> onUnauthorized(message))
      }
    }
  }
  val secureApi = secure { api }
  val routes = Route.seal( index ~ resources ~ signup ~ secureApi )
}