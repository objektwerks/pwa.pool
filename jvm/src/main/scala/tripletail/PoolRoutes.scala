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

  val logger = LoggerFactory.getLogger(PoolRoutes.getClass)

  def onUnauthorized(cause: String): Fault = {
    logger.error(cause)
    addFault(Fault(cause, Unauthorized.intValue))
  }

  def onInvalid(entity: Entity): Fault = {
    val cause = s"*** Invalid: $entity"
    logger.error(cause)
    addFault(Fault(cause, BadRequest.intValue))
  }

  def onFault(cause: String): Fault = {
    logger.error(cause)
    addFault(Fault(cause))
  }

  implicit val onException = ExceptionHandler {
    case NonFatal(error) =>
      extractRequestContext { context =>
        val cause = s"*** Handling ${context.request.uri} failed: ${error.getMessage}"
        context.request.discardEntityBytes(context.materializer)
        complete(InternalServerError -> onFault(cause))
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
        if (signup.isInvalid) complete(BadRequest -> onInvalid(signup))
        onSuccess(signUp(signup.email)) { licensee =>
          cacheLicensee(licensee)
          complete(OK -> SignedUp(licensee))
        }
      }
    }
  }
  val signin = path("signin") {
    post {
      entity(as[SignIn]) { signin =>
        if (signin.isInvalid) complete(BadRequest -> onInvalid(signin))
        onSuccess(signIn(signin.license, signin.email)) {
          case Some(licensee) =>
            cacheLicensee(licensee)
            complete(OK -> SignedIn(licensee))
          case None =>
            val cause = s"*** Unauthorized license: ${signin.license} and/or email: ${signin.email}"
            complete(Unauthorized -> onUnauthorized(cause))
        }
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[Licensee]) { licensee =>
        if (licensee.isInvalid) complete(BadRequest -> onInvalid(licensee))
        onSuccess(listPools(licensee.license)) { pools => complete(OK -> Pools(pools)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isInvalid) complete(BadRequest -> onInvalid(pool))
        onSuccess(addPool(pool)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isInvalid) complete(BadRequest -> onInvalid(pool))
        onSuccess(updatePool(pool)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listSurfaces(poolId.id)) { surfaces => complete(OK -> Surfaces(surfaces)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isInvalid) complete(BadRequest -> onInvalid(surface))
        onSuccess(addSurface(surface)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isInvalid) complete(BadRequest -> onInvalid(surface))
        onSuccess(updateSurface(surface)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listPumps(poolId.id)) { pumps => complete(OK -> Pumps(pumps)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isInvalid) complete(BadRequest -> onInvalid(pump))
        onSuccess(addPump(pump)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isInvalid) complete(BadRequest -> onInvalid(pump))
        onSuccess(updatePump(pump)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listTimers(poolId.id)) { timers => complete(OK -> Timers(timers)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isInvalid) complete(BadRequest -> onInvalid(timer))
        onSuccess(addTimer(timer)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isInvalid) complete(BadRequest -> onInvalid(timer))
        onSuccess(updateTimer(timer)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        if (timerId.isInvalid) complete(BadRequest -> onInvalid(timerId))
        onSuccess(listTimerSettings(timerId.id)) { timersettings => complete(OK -> TimerSettings(timersettings)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isInvalid) complete(BadRequest -> onInvalid(timerSetting))
        onSuccess(addTimerSetting(timerSetting)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isInvalid) complete(BadRequest -> onInvalid(timerSetting))
        onSuccess(updateTimerSetting(timerSetting)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listHeaters(poolId.id)) { heaters => complete(OK -> Heaters(heaters)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isInvalid) complete(BadRequest -> onInvalid(heater))
        onSuccess(addHeater(heater)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isInvalid) complete(BadRequest -> onInvalid(heater))
        onSuccess(updateHeater(heater)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val heatersettings = path("heatersettings") {
    post {
      entity(as[HeaterId]) { heaterId =>
        if (heaterId.isInvalid) complete(BadRequest -> onInvalid(heaterId))
        onSuccess(listHeaterSettings(heaterId.id)) { heaterSettings => complete(OK -> HeaterSettings(heaterSettings)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isInvalid) complete(BadRequest -> onInvalid(heaterSetting))
        onSuccess(addHeaterSetting(heaterSetting)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isInvalid) complete(BadRequest -> onInvalid(heaterSetting))
        onSuccess(updateHeaterSetting(heaterSetting)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listCleanings(poolId.id)) { cleanings => complete(OK -> Cleanings(cleanings)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isInvalid) complete(BadRequest -> onInvalid(cleaning))
        onSuccess(addCleaning(cleaning)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isInvalid) complete(BadRequest -> onInvalid(cleaning))
        onSuccess(updateCleaning(cleaning)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listMeasurements(poolId.id)) { measurements => complete(OK -> Measurements(measurements)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isInvalid) complete(BadRequest -> onInvalid(measurement))
        onSuccess(addMeasurement(measurement)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isInvalid) complete(BadRequest -> onInvalid(measurement))
        onSuccess(updateMeasurement(measurement)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listChemicals(poolId.id)) { chemicals => complete(OK -> Chemicals(chemicals)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isInvalid) complete(BadRequest -> onInvalid(chemical))
        onSuccess(addChemical(chemical)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isInvalid) complete(BadRequest -> onInvalid(chemical))
        onSuccess(updateChemical(chemical)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listSupplies(poolId.id)) { supplies => complete(OK -> Supplies(supplies)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isInvalid) complete(BadRequest -> onInvalid(supply))
        onSuccess(addSupply(supply)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isInvalid) complete(BadRequest -> onInvalid(supply))
        onSuccess(updateSupply(supply)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isInvalid) complete(BadRequest -> onInvalid(poolId))
        onSuccess(listRepairs(poolId.id)) { repairs => complete(OK -> Repairs(repairs)) }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isInvalid) complete(BadRequest -> onInvalid(repair))
        onSuccess(addRepair(repair)) { id => complete(OK -> Id(id)) }
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isInvalid) complete(BadRequest -> onInvalid(repair))
        onSuccess(updateRepair(repair)) { count => complete(OK -> Count(count)) }
      }
    }
  }
  val url = "/api/v1/tripletail"
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signin ~ pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heatersettings ~
      cleanings ~ measurements ~ chemicals ~ supplies ~ repairs
  }
  val secure = (route: Route) => headerValueByName(Licensee.licenseHeaderKey) { license =>
    onSuccess(isLicenseValid(license)) { isValid =>
      if (isValid) route
      else {
        val cause = s"*** Unauthorized license: $license"
        complete(Unauthorized -> onUnauthorized(cause))
      }
    }
  }
  val secureApi = secure { api }
  val routes = Route.seal( index ~ resources ~ signup ~ secureApi )
}