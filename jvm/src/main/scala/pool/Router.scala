package pool

import akka.actor.ActorRef

import akka.http.scaladsl.model.StatusCodes.{BadRequest, InternalServerError, OK, Unauthorized}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{ExceptionHandler, Route}

import com.typesafe.scalalogging.LazyLogging

import java.time.Instant

import scala.util.control.NonFatal

object Router {
  def apply(store: Store,
            cache: AccountCache,
            emailer: ActorRef): Router = new Router(store, cache, emailer)
}

class Router(store: Store,
             cache: AccountCache,
             emailer: ActorRef) extends LazyLogging with CorsHandler {
  import de.heikoseeberger.akkahttpupickle.UpickleSupport._
  import Serializers._
  import Validators._

  val toFault = (code: Int, cause: String) => {
    val fault = Fault(code, cause)
    logger.error(s"*** $fault")
    store.addFault(fault)
  }

  val onUnauthorizedRequestHandler = (cause: String) => toFault(Unauthorized.intValue, cause)

  val onBadRequestHandler = (cause: Serializable) => toFault(BadRequest.intValue, s"Bad Request: $cause")

  implicit val onExceptionHandler = ExceptionHandler {
    case NonFatal(error) =>
      extractRequestContext { context =>
        val fault = toFault(
          InternalServerError.intValue,
          s"Exception: url - ${context.request.uri} error - ${error.getMessage}"
        )
        context.request.discardEntityBytes(context.materializer)
        complete(InternalServerError -> fault)
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
          emailer ! register
          complete(OK -> Registering())
        } else complete(BadRequest -> onBadRequestHandler(register))
      }
    }
  }
  val login = path("login") {
    post {
      entity(as[Login]) { login =>
        if (login.isValid) {
          onSuccess(store.loginAccount(login.email, login.pin)) {
            case Some(account) =>
              cache.put(account)
              complete(OK -> LoggedIn(account))
            case None =>
              val cause = s"Unauthorized pin: ${login.pin}"
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
          onSuccess(store.deactivateAccount(deactivate.license)) {
            case Some(account) =>
              cache.remove(account)
              complete(OK -> Deactivated(account))
            case None =>
              val cause = s"Unauthorized license: ${deactivate.license}"
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
          onSuccess(store.reactivateAccount(reactivate.license)) {
            case Some(account) =>
              cache.put(account)
              complete(OK -> Reactivated(account))
            case None =>
              val cause = s"Unauthorized license: ${reactivate.license}"
              complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
          }
        } else complete(BadRequest -> onBadRequestHandler(reactivate))
      }
    }
  }
  val pools = path("pools") {
    post {
      entity(as[License]) { license =>
        if (license.isValid) onSuccess(store.listPools(license.key)) { pools => complete(OK -> Pools(pools)) }
        else {
          val cause = s"Unauthorized license: $license"
          complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
        }
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isValid) onSuccess(store.addPool(pool)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(pool))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pool]) { pool =>
        if (pool.isValid) onSuccess(store.updatePool(pool)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(pool))
      }
    }
  }
  val surfaces = path("surfaces") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listSurfaces(poolId.id)) { surfaces => complete(OK -> Surfaces(surfaces)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) onSuccess(store.addSurface(surface)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(surface))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Surface]) { surface =>
        if (surface.isValid) onSuccess(store.updateSurface(surface)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(surface))
      }
    }
  }
  val pumps = path("pumps") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listPumps(poolId.id)) { pumps => complete(OK -> Pumps(pumps)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) onSuccess(store.addPump(pump)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(pump))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Pump]) { pump =>
        if (pump.isValid) onSuccess(store.updatePump(pump)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(pump))
      }
    }
  }
  val timers = path("timers") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listTimers(poolId.id)) { timers => complete(OK -> Timers(timers)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) onSuccess(store.addTimer(timer)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(timer))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Timer]) { timer =>
        if (timer.isValid) onSuccess(store.updateTimer(timer)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(timer))
      }
    }
  }
  val timersettings = path("timersettings") {
    post {
      entity(as[TimerId]) { timerId =>
        if (timerId.isValid) onSuccess(store.listTimerSettings(timerId.id)) { timersettings => complete(OK -> TimerSettings(timersettings)) }
        else complete(BadRequest -> onBadRequestHandler(timerId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) onSuccess(store.addTimerSetting(timerSetting)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(timerSetting))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[TimerSetting]) { timerSetting =>
        if (timerSetting.isValid) onSuccess(store.updateTimerSetting(timerSetting)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(timerSetting))
      }
    }
  }
  val heaters = path("heaters") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listHeaters(poolId.id)) { heaters => complete(OK -> Heaters(heaters)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) onSuccess(store.addHeater(heater)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(heater))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Heater]) { heater =>
        if (heater.isValid) onSuccess(store.updateHeater(heater)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(heater))
      }
    }
  }
  val heatersettings = path("heatersettings") {
    post {
      entity(as[HeaterId]) { heaterId =>
        if (heaterId.isValid) onSuccess(store.listHeaterSettings(heaterId.id)) { heaterSettings => complete(OK -> HeaterSettings(heaterSettings)) }
        else complete(BadRequest -> onBadRequestHandler(heaterId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isValid) onSuccess(store.addHeaterSetting(heaterSetting)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(heaterSetting))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[HeaterSetting]) { heaterSetting =>
        if (heaterSetting.isValid) onSuccess(store.updateHeaterSetting(heaterSetting)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(heaterSetting))
      }
    }
  }
  val measurements = path("measurements") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listMeasurements(poolId.id)) { measurements => complete(OK -> Measurements(measurements)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isValid) onSuccess(store.addMeasurement(measurement)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(measurement))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Measurement]) { measurement =>
        if (measurement.isValid) onSuccess(store.updateMeasurement(measurement)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(measurement))
      }
    }
  }
  val cleanings = path("cleanings") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listCleanings(poolId.id)) { cleanings => complete(OK -> Cleanings(cleanings)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isValid) onSuccess(store.addCleaning(cleaning)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(cleaning))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Cleaning]) { cleaning =>
        if (cleaning.isValid) onSuccess(store.updateCleaning(cleaning)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(cleaning))
      }
    }
  }
  val chemicals = path("chemicals") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listChemicals(poolId.id)) { chemicals => complete(OK -> Chemicals(chemicals)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isValid) onSuccess(store.addChemical(chemical)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(chemical))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Chemical]) { chemical =>
        if (chemical.isValid) onSuccess(store.updateChemical(chemical)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(chemical))
      }
    }
  }
  val supplies = path("supplies") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listSupplies(poolId.id)) { supplies => complete(OK -> Supplies(supplies)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isValid) onSuccess(store.addSupply(supply)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(supply))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Supply]) { supply =>
        if (supply.isValid) onSuccess(store.updateSupply(supply)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(supply))
      }
    }
  }
  val repairs = path("repairs") {
    post {
      entity(as[PoolId]) { poolId =>
        if (poolId.isValid) onSuccess(store.listRepairs(poolId.id)) { repairs => complete(OK -> Repairs(repairs)) }
        else complete(BadRequest -> onBadRequestHandler(poolId))
      }
    }
  } ~ pathSuffix("add") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isValid) onSuccess(store.addRepair(repair)) { id => complete(OK -> Id(id)) }
        else complete(BadRequest -> onBadRequestHandler(repair))
      }
    }
  } ~ pathSuffix("update") {
    post {
      entity(as[Repair]) { repair =>
        if (repair.isValid) onSuccess(store.updateRepair(repair)) { count => complete(OK -> Count(count)) }
        else complete(BadRequest -> onBadRequestHandler(repair))
      }
    }
  }

  val public = now ~ register ~ login ~ deactivate ~ reactivate

  val api = pathPrefix("api" / "v1" / "pool") {
    pools ~ surfaces ~ pumps ~ timers ~ timersettings ~ heaters ~ heatersettings ~
      measurements ~ cleanings ~ chemicals ~ supplies ~ repairs
  }

  val secure = (route: Route) => headerValueByName(Account.licenseHeader) { license =>
    if (cache.isAccountActivated(license)) route
    else {
      val cause = s"Unauthorized license is not activated: $license"
      complete(Unauthorized -> onUnauthorizedRequestHandler(cause))
    }
  }
  val secureApi = secure { api }

  val routes = corsHandler { public ~ secureApi }
}