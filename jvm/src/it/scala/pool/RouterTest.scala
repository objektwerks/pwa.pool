package pool

import akka.actor.ActorSystem
import akka.actor.Props

import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes.{BadRequest, OK, Unauthorized}
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}

import akka.testkit.TestDuration

import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.StrictLogging

import org.scalatest.BeforeAndAfterAll
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.sys.process.Process
import scala.concurrent.Await

class RouterTest extends AnyWordSpec
  with BeforeAndAfterAll
  with Matchers
  with ScalatestRouteTest
  with StrictLogging {
  Process("psql -d pool -f ddl.sql").run().exitValue()

  val conf = ConfigFactory.load("test.server.conf")
  val actorRefFactory = ActorSystem.create(conf.getString("server.name"), conf.getConfig("akka"))
  implicit val dispatcher = system.dispatcher
  implicit val timeout = RouteTestTimeout(10.seconds dilated)

  val store = Store(conf)
  val cache = AccountCache()
  val emailer = system.actorOf(Props(classOf[Emailer], conf, store), name = "emailer")
  val router = Router(store, cache, emailer)
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
  val apiUrl = conf.getString("server.apiUrl")
  val server = Http()
    .newServerAt(host, port)
    .bindFlow(router.routes)
    .map { server =>
      logger.info(s"*** Server host: ${server.localAddress.toString}")
      logger.info(s"*** Server api url: $apiUrl")
      server
  }

  override protected def afterAll(): Unit =
    server
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("*** Server shutting down...")
        actorRefFactory.terminate()
        Await.result(actorRefFactory.whenTerminated, 10.seconds)
        logger.info("*** Server shutdown.")
      }

  import de.heikoseeberger.akkahttpupickle.UpickleSupport._
  import Serializers._
  import Validators._
  import DateTime._

  var account: Account = _
  var licenseHeader: RawHeader = _
  var poolid: PoolId = _
  var timerid: TimerId = _
  var heaterid: HeaterId = _

  "now" should {
    "get now" in {
      Post("/now") ~> router.routes ~> check {
        status shouldBe OK
        responseAs[String].nonEmpty shouldBe true
      }
    }
  }

  "register" should {
    "post to registered" in {
      Post("/register", Register(email = conf.getString("email.from"))) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Registering].inProgress shouldBe true
        Thread.sleep(9000L) // Required for Emailer to: 1. send email, 2. add email, 3. register account
        account = Await.result(store.listAccounts, 3 seconds ).head
      }
    }
  }

  "login" should {
    "post to loggedin" in {
      Post("/login", Login(account.email, account.pin)) ~> router.routes ~> check {
        status shouldBe OK
        account = responseAs[LoggedIn].account
        licenseHeader = RawHeader(Account.licenseHeader, account.license)
        account.isActivated shouldBe true
      }
    }
  }

  "pools" should {
    "post to id, count, pools" in {
      var pool = Pool(license = account.license, name = "pool-a", built = localDateToInt(1991, 3, 13), volume = 10000)
      Post(apiUrl + "/pools/add", pool) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        pool = pool.copy(id = responseAs[Id].id)
        pool.id should be > 0
        poolid = PoolId(pool.id)
      }
      pool = pool.copy(volume = 9000)
      Post(apiUrl + "/pools/update", pool) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/pools", account.deriveLicense) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Pools].pools.length shouldEqual 1
      }
    }
  }

  "surfaces" should {
    "post to id, count, surfaces" in {
      var surface = Surface(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), kind = "concrete")
      Post(apiUrl + "/surfaces/add", surface) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        surface = surface.copy(id = responseAs[Id].id)
        surface.id should be > 0
      }
      surface = surface.copy(kind = "pebble")
      Post(apiUrl + "/surface/update", surface) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/surfaces", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Surfaces].surfaces.length shouldEqual 1
      }
    }
  }

  "pumps" should {
    "post to id, count, pumps" in {
      var pump = Pump(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "rocket")
      Post(apiUrl + "/pumps/add", pump) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        pump = pump.copy(id = responseAs[Id].id)
        pump.id should be > 0
      }
      pump = pump.copy(model = "iron")
      Post(apiUrl + "/pumps/update", pump) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/pumps", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Pumps].pumps.length shouldEqual 1
      }
    }
  }

  "timers" should {
    "post to id, count, timers" in {
      var timer = Timer(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "timex")
      Post(apiUrl + "/timers/add", timer) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        timer = timer.copy(id = responseAs[Id].id)
        timer.id should be > 0
        timerid = TimerId(timer.id)
      }
      timer = timer.copy(model = "rolex")
      Post(apiUrl + "/timers/update", timer) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/timers", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Timers].timers.length shouldEqual 1
      }
    }
  }

  "timersettings" should {
    "post to id, count, timersettings" in {
      var timerSetting = TimerSetting(timerId = timerid.id,
        created = localDateToInt(1991, 3, 13),
        timeOn = localTimeToInt(8, 15),
        timeOff = localTimeToInt(17, 15))
      Post(apiUrl + "/timersettings/add", timerSetting) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        timerSetting = timerSetting.copy(id = responseAs[Id].id)
        timerSetting.id should be > 0
      }
      timerSetting = timerSetting.copy(timeOff = localTimeToInt(17, 30))
      Post(apiUrl + "/timersettings/update", timerSetting) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/timersettings", timerid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[TimerSettings].timerSettings.length shouldEqual 1
      }
    }
  }

  "heaters" should {
    "post to id, count, heaters" in {
      var heater = Heater(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "hotty")
      Post(apiUrl + "/heaters/add", heater) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        heater = heater.copy(id = responseAs[Id].id)
        heater.id should be > 0
        heaterid = HeaterId(heater.id)
      }
      heater = heater.copy(model = "burner")
      Post(apiUrl + "/heaters/update", heater) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/heaters", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Heaters].heaters.length shouldEqual 1
      }
    }
  }

  "heatersettings" should {
    "post to id, count, heatersettings" in {
      var heaterOn = HeaterSetting(heaterId = heaterid.id, temp = 85, dateOn = localDateToInt(1991, 3, 13))
      Post(apiUrl + "/heatersettings/add", heaterOn) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        heaterOn = heaterOn.copy(id = responseAs[Id].id)
        heaterOn.id should be > 0
      }
      heaterOn = heaterOn.copy(dateOff = localDateToInt(1991, 9, 13))
      Post(apiUrl + "/heatersettings/update", heaterOn) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/heatersettings", heaterid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[HeaterSettings].heaterSettings.length shouldEqual 1
      }
    }
  }

  "measurements" should {
    "post to id, count, measurements" in {
      var measurement = Measurement(poolId = poolid.id, measured = localDateToInt(1991, 3, 13))
      Post(apiUrl + "/measurements/add", measurement) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        measurement = measurement.copy(id = responseAs[Id].id)
        measurement.id should be > 0
      }
      measurement = measurement.copy(temp = 90)
      Post(apiUrl + "/measurements/update", measurement) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/measurements", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Measurements].measurements.length shouldEqual 1
      }
    }
  }

  "cleanings" should {
    "post to id, count, cleanings" in {
      var cleaning = Cleaning(poolId = poolid.id, cleaned = localDateToInt(1991, 3, 13))
      Post(apiUrl + "/cleanings/add", cleaning) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        cleaning = cleaning.copy(id = responseAs[Id].id)
        cleaning.id should be > 0
      }
      cleaning = cleaning.copy(deck = true)
      Post(apiUrl + "/cleanings/update", cleaning) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/cleanings", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Cleanings].cleanings.length shouldEqual 1
      }
    }
  }

  "chemicals" should {
    "post to id, count, chemicals" in {
      var _chemical = Chemical(poolId = poolid.id,
        added = localDateToInt(1991, 3, 13),
        chemical = "chlorine",
        amount = 1.25,
        unit = "gallon")
      Post(apiUrl + "/chemicals/add", _chemical) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        _chemical = _chemical.copy(id = responseAs[Id].id)
        _chemical.id should be > 0
      }
      _chemical = _chemical.copy(amount = 1.50)
      Post(apiUrl + "/chemicals/update", _chemical) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/chemicals", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Chemicals].chemicals.length shouldEqual 1
      }
    }
  }

  "supplies" should {
    "post to id, count, supplies" in {
      var supply = Supply(poolId = poolid.id,
        purchased = localDateToInt(1991, 3, 13),
        cost = 5.00,
        item = "chlorine",
        amount = 1.25,
        unit = "gallon")
      Post(apiUrl + "/supplies/add", supply) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        supply = supply.copy(id = responseAs[Id].id)
        supply.id should be > 0
      }
      supply = supply.copy(amount = 1.50)
      Post(apiUrl + "/supplies/update", supply) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/supplies", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Supplies].supplies.length shouldEqual 1
      }
    }
  }

  "repairs" should {
    "post to id, count, repairs" in {
      var _repair = Repair(poolId = poolid.id,
        repaired = localDateToInt(1991, 3, 13),
        cost = 50.50,
        repair = "paint pool deck")
      Post(apiUrl + "/repairs/add", _repair) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        _repair = _repair.copy(id = responseAs[Id].id)
        _repair.id should be > 0
      }
      _repair = _repair.copy(repair = "repaint pool deck")
      Post(apiUrl + "/repairs/update", _repair) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(apiUrl + "/repairs", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Repairs].repairs.length shouldEqual 1
      }
    }
  }

  "invalid" should {
    "post to register, fault" in {
      Post("/register", Register(email = "invalid.email")) ~> router.routes ~> check {
        status shouldBe BadRequest
        val fault = responseAs[Fault]
        fault.cause.nonEmpty shouldBe true
        fault.code shouldBe 400
      }
    }
  }

  "unauthorized" should {
    "post to pools, fault" in {
      Post(apiUrl + "/pools", License("")) ~> addHeader(licenseHeader.copy(value = "")) ~> router.routes ~> check {
        status shouldBe Unauthorized
        val fault = responseAs[Fault]
        fault.cause.nonEmpty shouldBe true
        fault.code shouldBe 401
      }
    }
  }

  "deactivate" should {
    "post to deactivated" in {
      Post("/deactivate", Deactivate(account.license)) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Deactivated].account.isDeactivated shouldBe true
      }
    }
  }

  "reactivate" should {
    "post to reactivated" in {
      Post("/reactivate", Reactivate(account.license)) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Reactivated].account.isActivated shouldBe true
      }
    }
  }
}