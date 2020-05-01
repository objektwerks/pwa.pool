package pool

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import com.typesafe.config.ConfigFactory
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

class RouterTest extends AnyWordSpec with Matchers with ScalatestRouteTest {
  val logger = LoggerFactory.getLogger(getClass)
  val conf = ConfigFactory.load("test.server.conf")
  val actorRefFactory = ActorSystem.create(conf.getString("server.name"), conf.getConfig("akka"))
  implicit val dispatcher = system.dispatcher
  implicit val timeout = RouteTestTimeout(10.seconds dilated)

  val store = Store(conf)
  val cache = LicenseeCache(store)
  val emailer = system.actorOf(Props(classOf[Emailer], conf), name = "emailer")
  val router = Router(store, cache, emailer)
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
  Http()
    .bindAndHandle(router.routes, host, port)
    .map { server =>
      logger.info(s"*** Server integration test host: ${server.localAddress.toString}")
    }

  import de.heikoseeberger.akkahttpupickle.{UpickleSupport => Upickle}
  import Upickle._
  import DateTime._
  import StatusCodes._
  import Serializers._
  import Validators._

  val url = router.url
  var licensee: Licensee = _
  var licenseHeader: RawHeader = _
  var poolid: PoolId = _
  var timerid: TimerId = _
  var heaterid: HeaterId = _

  "signup" should {
    "post to signedup" in {
      Post("/signup", SignUp(emailAddress = conf.getString("email.testUser"))) ~> router.routes ~> check {
        status shouldBe OK
        licensee = responseAs[SignedUp].licensee
        licensee.isActivated shouldBe false
      }
    }
  }
  "activatelicensee" should {
    "post to activated licensee" in {
      Post("/activatelicensee", ActivateLicensee(licensee.license, licensee.emailAddress)) ~> router.routes ~> check {
        status shouldBe OK
        licensee = responseAs[LicenseeActivated].licensee
        licenseHeader = RawHeader(Licensee.headerLicenseKey, licensee.license)
        licensee.isActivated shouldBe true
      }
    }
  }

  "signin" should {
    "post to signedin" in {
      Post(url + "/signin", SignIn(licensee.license, licensee.emailAddress)) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[SignedIn].licensee shouldEqual licensee
        licensee.isActivated shouldBe true
      }
    }
  }

  "pools" should {
    "post to id, count, pools" in {
      var pool = Pool(license = licensee.license, built = localDateToInt(1991, 3, 13), lat = 26.862631, lon = -82.288834, volume = 10000)
      Post(url + "/pools/add", pool) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        pool = pool.copy(id = responseAs[Id].id)
        pool.id should be > 0
        poolid = PoolId(pool.id)
      }
      pool = pool.copy(volume = 9000)
      Post(url + "/pools/update", pool) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/pools", licensee.toLicense) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Pools].pools.length shouldEqual 1
      }
    }
  }

  "surfaces" should {
    "post to id, count, surfaces" in {
      var surface = Surface(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), kind = "concrete")
      Post(url + "/surfaces/add", surface) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        surface = surface.copy(id = responseAs[Id].id)
        surface.id should be > 0
      }
      surface = surface.copy(kind = "pebble")
      Post(url + "/surface/update", surface) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/surfaces", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Surfaces].surfaces.length shouldEqual 1
      }
    }
  }

  "pumps" should {
    "post to id, count, pumps" in {
      var pump = Pump(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "rocket")
      Post(url + "/pumps/add", pump) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        pump = pump.copy(id = responseAs[Id].id)
        pump.id should be > 0
      }
      pump = pump.copy(model = "iron")
      Post(url + "/pumps/update", pump) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/pumps", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Pumps].pumps.length shouldEqual 1
      }
    }
  }

  "timers" should {
    "post to id, count, timers" in {
      var timer = Timer(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "timex")
      Post(url + "/timers/add", timer) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        timer = timer.copy(id = responseAs[Id].id)
        timer.id should be > 0
        timerid = TimerId(timer.id)
      }
      timer = timer.copy(model = "rolex")
      Post(url + "/timers/update", timer) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/timers", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
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
      Post(url + "/timersettings/add", timerSetting) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        timerSetting = timerSetting.copy(id = responseAs[Id].id)
        timerSetting.id should be > 0
      }
      timerSetting = timerSetting.copy(timeOff = localTimeToInt(17, 30))
      Post(url + "/timersettings/update", timerSetting) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/timersettings", timerid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[TimerSettings].timerSettings.length shouldEqual 1
      }
    }
  }

  "heaters" should {
    "post to id, count, heaters" in {
      var heater = Heater(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "hotty")
      Post(url + "/heaters/add", heater) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        heater = heater.copy(id = responseAs[Id].id)
        heater.id should be > 0
        heaterid = HeaterId(heater.id)
      }
      heater = heater.copy(model = "burner")
      Post(url + "/heaters/update", heater) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/heaters", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Heaters].heaters.length shouldEqual 1
      }
    }
  }

  "heatersettings" should {
    "post to id, count, heatersettings" in {
      var heaterOn = HeaterSetting(heaterId = heaterid.id, temp = 85, dateOn = localDateToInt(1991, 3, 13))
      Post(url + "/heatersettings/add", heaterOn) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        heaterOn = heaterOn.copy(id = responseAs[Id].id)
        heaterOn.id should be > 0
      }
      heaterOn = heaterOn.copy(dateOff = localDateToInt(1991, 9, 13))
      Post(url + "/heatersettings/update", heaterOn) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/heatersettings", heaterid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[HeaterSettings].heaterSettings.length shouldEqual 1
      }
    }
  }

  "measurements" should {
    "post to id, count, measurements" in {
      var measurement = Measurement(poolId = poolid.id, measured = localDateToInt(1991, 3, 13))
      Post(url + "/measurements/add", measurement) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        measurement = measurement.copy(id = responseAs[Id].id)
        measurement.id should be > 0
      }
      measurement = measurement.copy(temp = 90)
      Post(url + "/measurements/update", measurement) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/measurements", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Measurements].measurements.length shouldEqual 1
      }
    }
  }

  "cleanings" should {
    "post to id, count, cleanings" in {
      var cleaning = Cleaning(poolId = poolid.id, cleaned = localDateToInt(1991, 3, 13))
      Post(url + "/cleanings/add", cleaning) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        cleaning = cleaning.copy(id = responseAs[Id].id)
        cleaning.id should be > 0
      }
      cleaning = cleaning.copy(deck = true)
      Post(url + "/cleanings/update", cleaning) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/cleanings", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
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
      Post(url + "/chemicals/add", _chemical) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        _chemical = _chemical.copy(id = responseAs[Id].id)
        _chemical.id should be > 0
      }
      _chemical = _chemical.copy(amount = 1.50)
      Post(url + "/chemicals/update", _chemical) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/chemicals", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
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
      Post(url + "/supplies/add", supply) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        supply = supply.copy(id = responseAs[Id].id)
        supply.id should be > 0
      }
      supply = supply.copy(amount = 1.50)
      Post(url + "/supplies/update", supply) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/supplies", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
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
      Post(url + "/repairs/add", _repair) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        _repair = _repair.copy(id = responseAs[Id].id)
        _repair.id should be > 0
      }
      _repair = _repair.copy(repair = "repaint pool deck")
      Post(url + "/repairs/update", _repair) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/repairs", poolid) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        responseAs[Repairs].repairs.length shouldEqual 1
      }
    }
  }

  "invalid" should {
    "post to signedup, fault" in {
      Post("/signup", SignUp(emailAddress = "")) ~> router.routes ~> check {
        status shouldBe BadRequest
        val fault = responseAs[Fault]
        fault.cause.nonEmpty shouldBe true
        fault.code shouldBe 400
      }
    }
  }

  "unauthorized" should {
    "post to pools, fault" in {
      Post(url + "/pools", License("")) ~> addHeader(licenseHeader.copy(value = "")) ~> router.routes ~> check {
        status shouldBe Unauthorized
        val fault = responseAs[Fault]
        fault.cause.nonEmpty shouldBe true
        fault.code shouldBe 401
      }
    }
  }

  "deactivatelicensee" should {
    "post to deactivated licensee" in {
      Post(url + "/deactivatelicensee", DeactivateLicensee(licensee.license, licensee.emailAddress)) ~> addHeader(licenseHeader) ~> router.routes ~> check {
        status shouldBe OK
        licensee = responseAs[LicenseeDeactivated].licensee
        licensee.isDeactivated shouldBe true
      }
    }
  }
}