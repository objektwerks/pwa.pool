package pool

import java.time.Instant
import java.util.concurrent.TimeUnit

import akka.actor.ActorRef
import akka.pattern._
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}
import akka.util.Timeout

import org.slf4j.LoggerFactory

import scala.util.control.NonFatal

object Router {
  def apply(store: Store, cache: AccountCache, emailer: ActorRef): Router = new Router(store, cache, emailer)
}

class Router(store: Store, cache: AccountCache, emailer: ActorRef) extends CorsHandler {
  import de.heikoseeberger.akkahttpupickle.UpickleSupport._
  import Serializers._
  import Validators._
  import StatusCodes._
  import cache._
  import store._

  val logger = LoggerFactory.getLogger(getClass)

  val onUnauthorizedRequestHandler = (cause: String) => {
    val error = s"*** Unauthorized Request: $cause"
    logger.error(error)
    addFault(Fault(code = Unauthorized.intValue, cause = error))
  }

  val onBadRequestHandler = (serializable: Serializable) => {
    val cause = s"*** Bad Request: $serializable"
    logger.error(cause)
    addFault(Fault(code = BadRequest.intValue, cause = cause))
  }

  implicit val onExceptionHandler = ExceptionHandler {
    case NonFatal(error) =>
      extractRequestContext { context =>
        val cause = s"*** Exception: on - ${context.request.uri} with - ${error.getMessage}"
        logger.error(cause)
        context.request.discardEntityBytes(context.materializer)
        complete(InternalServerError -> addFault(Fault(cause = cause)))
      }
  }
    
  val now = path("now") {
    post {
      complete(OK -> Instant.now.toString)
    }
  }
  val register = path("register") {
    post {
      entity(as[Register]) { register =>
        if (register.isValid) {
          implicit val timeout = new Timeout(10, TimeUnit.SECONDS)
          val licensee = Account(register.email)
          onSuccess( emailer ? SendEmail(licensee) ) {
            case Some(_) => onSuccess(registerAccount(licensee)) {
              licensee => complete(OK -> Registered(licensee))
            }
            case _ => complete(BadRequest -> onBadRequestHandler(register))
          }
        } else complete(BadRequest -> onBadRequestHandler(register))
      }
    }
  }
  val login = path("login") {
    post {
      entity(as[Login]) { login =>
        if (login.isValid) {
          onSuccess(loginAccount(login.pin)) {
            case Some(account) =>
              cacheAccount(account)
              complete(OK -> LoggedIn(account))
            case None =>
              val cause = s"*** Unauthorized pin: ${login.pin}"
              complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
          }
        } else complete(BadRequest -> onBadRequestHandler(login))
      }
    }
  }
  val deactivate = path("deactivate") {
    post {
      entity(as[Deactivate]) { deactivate =>
        if (deactivate.isValid) {
          onSuccess(deactivateAccount(deactivate.license)) {
            case Some(account) =>
              decacheAccount(account)
              complete(OK -> Deactivated(account))
            case None =>
              val cause = s"*** Unauthorized license: ${deactivate.license}"
              complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
          }
        } else complete(BadRequest -> onBadRequestHandler(deactivate))
      }
    }
  }
  val reactivate = path("reactivate") {
    post {
      entity(as[Reactivate]) { reactivate =>
        if (reactivate.isValid) {
          onSuccess(reactivateAccount(reactivate.license)) {
            case Some(licensee) =>
              complete(OK -> Reactivated(licensee))
            case None =>
              val cause = s"*** Unauthorized license: ${reactivate.license}"
              complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
          }
        } else complete(BadRequest -> onBadRequestHandler(reactivate))
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[License]) { license =>
        if (license.isValid) onSuccess(listPools(license.key)) { pools => complete(OK -> Pools(pools)) }
        else {
          val cause = s"*** Unauthorized license: $license"
          complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isValid) onSuccess(addPool(pool)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(pool))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isValid) onSuccess(updatePool(pool)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(pool))
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listSurfaces(poolId.id)) { surfaces => complete(OK -> Surfaces(surfaces)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) onSuccess(addSurface(surface)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(surface))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) onSuccess(updateSurface(surface)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(surface))
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listPumps(poolId.id)) { pumps => complete(OK -> Pumps(pumps)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) onSuccess(addPump(pump)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(pump))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) onSuccess(updatePump(pump)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(pump))
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listTimers(poolId.id)) { timers => complete(OK -> Timers(timers)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) onSuccess(addTimer(timer)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(timer))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) onSuccess(updateTimer(timer)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(timer))
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        if (timerId.isValid) onSuccess(listTimerSettings(timerId.id)) { timersettings => complete(OK -> TimerSettings(timersettings)) }
        else complete(BadRequest -> onBadRequestHandler(timerId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) onSuccess(addTimerSetting(timerSetting)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(timerSetting))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) onSuccess(updateTimerSetting(timerSetting)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(timerSetting))
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listHeaters(poolId.id)) { heaters => complete(OK -> Heaters(heaters)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) onSuccess(addHeater(heater)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(heater))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) onSuccess(updateHeater(heater)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(heater))
      }
    }
  }
  val heatersettings = path("heatersettings") {
    post {
      entity(as[HeaterId]) { heaterId =>
        if (heaterId.isValid) onSuccess(listHeaterSettings(heaterId.id)) { heaterSettings => complete(OK -> HeaterSettings(heaterSettings)) }
        else complete(BadRequest -> onBadRequestHandler(heaterId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isValid) onSuccess(addHeaterSetting(heaterSetting)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(heaterSetting))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isValid) onSuccess(updateHeaterSetting(heaterSetting)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(heaterSetting))
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listMeasurements(poolId.id)) { measurements => complete(OK -> Measurements(measurements)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isValid) onSuccess(addMeasurement(measurement)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(measurement))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isValid) onSuccess(updateMeasurement(measurement)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(measurement))
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listCleanings(poolId.id)) { cleanings => complete(OK -> Cleanings(cleanings)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isValid) onSuccess(addCleaning(cleaning)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(cleaning))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isValid) onSuccess(updateCleaning(cleaning)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(cleaning))
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listChemicals(poolId.id)) { chemicals => complete(OK -> Chemicals(chemicals)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isValid) onSuccess(addChemical(chemical)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(chemical))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isValid) onSuccess(updateChemical(chemical)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(chemical))
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listSupplies(poolId.id)) { supplies => complete(OK -> Supplies(supplies)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isValid) onSuccess(addSupply(supply)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(supply))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isValid) onSuccess(updateSupply(supply)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(supply))
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(listRepairs(poolId.id)) { repairs => complete(OK -> Repairs(repairs)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isValid) onSuccess(addRepair(repair)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(repair))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isValid) onSuccess(updateRepair(repair)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(repair))
      }
    }
  }

  val public = now ~ register ~ login ~ deactivate ~ reactivate

  val api = pathPrefix("api" / "v1" / "pool") {
    pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heatersettings ~
      measurements ~ cleanings ~ chemicals ~ supplies ~ repairs
  }

  val secure = (route: Route) => headerValueByName(Account.headerLicenseKey) { license =>
    onSuccess(isAccountActived(license)) { isActivated =>
      if (isActivated) route
      else {
        val cause = s"*** License is not activated: $license"
        complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
      }
    }
  }
  val secureApi = secure { api }

  val routes = corsHandler { public ~ secureApi }
}