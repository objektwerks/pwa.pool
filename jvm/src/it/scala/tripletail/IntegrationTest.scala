package tripletail

import de.heikoseeberger.akkahttpupickle.{UpickleSupport => Upickle}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.{RouteTestTimeout, ScalatestRouteTest}
import akka.testkit.TestDuration
import com.typesafe.config.ConfigFactory
import org.scalatest.{Matchers, WordSpec}
import org.slf4j.LoggerFactory

import scala.concurrent.duration._
import scala.language.postfixOps

class IntegrationTest extends WordSpec with Matchers with ScalatestRouteTest {
  val logger = LoggerFactory.getLogger(getClass.getSimpleName)
  val actorRefFactory = ActorSystem.create(getClass.getSimpleName)
  implicit val dispatcher = system.dispatcher
  implicit val timeout = RouteTestTimeout(10.seconds dilated)

  val conf = ConfigFactory.load("it.test.conf")
  val store = PoolStore(conf)
  val cache = LicenseeCache(store)
  val routes = PoolRoutes(store, cache)
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
  Http()
    .bindAndHandle(routes.routes, host, port)
    .map { server =>
      logger.info(s"*** Pool app integration test host: ${server.localAddress.toString}")
    }

  import Upickle._
  import DateTime._
  import StatusCodes._
  import Serializers._
  import Validators._

  val url = routes.url
  var licensee: Licensee = _
  var licenseHeader: RawHeader = _
  var poolid: PoolId = _
  var timerid: TimerId = _
  var heaterid: HeaterId = _

  "signup" should {
    "post to signedup" in {
      Post("/signup", SignUp(email = "test@test.com")) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        licensee = responseAs[SignedUp].licensee
        licenseHeader = RawHeader(Licensee.licenseHeaderKey, licensee.license)
        licensee.isValid shouldBe true
      }
    }
  }

  "signin" should {
    "post to signedin" in {
      Post(url + "/signin", SignIn(licensee.license, licensee.email)) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[SignedIn].licensee shouldEqual licensee
      }
    }
  }

  "pools" should {
    "post to id, count, pools" in {
      var pool = Pool(license = licensee.license, built = localDateToInt(1991, 3, 13), lat = 26.862631, lon = -82.288834, volume = 10000)
      Post(url + "/pools/add", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        pool = pool.copy(id = responseAs[Id].id)
        pool.id should be > 0
        poolid = PoolId(pool.id)
      }
      pool = pool.copy(volume = 9000)
      Post(url + "/pools/update", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/pools", licensee) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Pools].pools.length shouldEqual 1
      }
    }
  }

  "surfaces" should {
    "post to id, count, surfaces" in {
      var surface = Surface(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), kind = "concrete")
      Post(url + "/surfaces/add", surface) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        surface = surface.copy(id = responseAs[Id].id)
        surface.id should be > 0
      }
      surface = surface.copy(kind = "pebble")
      Post(url + "/surface/update", surface) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/surfaces", poolid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Surfaces].surfaces.length shouldEqual 1
      }
    }
  }

  "pumps" should {
    "post to id, count, pumps" in {
      var pump = Pump(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "rocket")
      Post(url + "/pumps/add", pump) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        pump = pump.copy(id = responseAs[Id].id)
        pump.id should be > 0
      }
      pump = pump.copy(model = "iron")
      Post(url + "/pumps/update", pump) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/pumps", poolid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Pumps].pumps.length shouldEqual 1
      }
    }
  }

  "timers" should {
    "post to id, count, timers" in {
      var timer = Timer(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "timex")
      Post(url + "/timers/add", timer) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        timer = timer.copy(id = responseAs[Id].id)
        timer.id should be > 0
        timerid = TimerId(timer.id)
      }
      timer = timer.copy(model = "rolex")
      Post(url + "/timers/update", timer) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/timers", poolid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
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
      Post(url + "/timersettings/add", timerSetting) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        timerSetting = timerSetting.copy(id = responseAs[Id].id)
        timerSetting.id should be > 0
      }
      timerSetting = timerSetting.copy(timeOff = localTimeToInt(17, 30))
      Post(url + "/timersettings/update", timerSetting) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/timersettings", timerid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[TimerSettings].timerSettings.length shouldEqual 1
      }
    }
  }

  "heaters" should {
    "post to id, count, heaters" in {
      var heater = Heater(poolId = poolid.id, installed = localDateToInt(1991, 3, 13), model = "hotty")
      Post(url + "/heaters/add", heater) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        heater = heater.copy(id = responseAs[Id].id)
        heater.id should be > 0
        heaterid = HeaterId(heater.id)
      }
      heater = heater.copy(model = "burner")
      Post(url + "/heaters/update", heater) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/heaters", poolid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Heaters].heaters.length shouldEqual 1
      }
    }
  }

  "heatersettings" should {
    "post to id, count, heatersettings" in {
      var heaterOn = HeaterSetting(heaterId = heaterid.id, temp = 85, dateOn = localDateToInt(1991, 3, 13))
      Post(url + "/heatersettings/add", heaterOn) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        heaterOn = heaterOn.copy(id = responseAs[Id].id)
        heaterOn.id should be > 0
      }
      heaterOn = heaterOn.copy(dateOff = localDateToInt(1991, 9, 13))
      Post(url + "/heatersettings/update", heaterOn) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/heatersettings", heaterid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[HeaterSettings].heaterSettings.length shouldEqual 1
      }
    }
  }

  "cleanings" should {
    "post to id, count, cleanings" in {
      var cleaning = Cleaning(poolId = poolid.id, cleaned = localDateToInt(1991, 3, 13))
      Post(url + "/cleanings/add", cleaning) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        cleaning = cleaning.copy(id = responseAs[Id].id)
        cleaning.id should be > 0
      }
      cleaning = cleaning.copy(deck = true)
      Post(url + "/cleanings/update", cleaning) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Count].count shouldEqual 1
      }
      Post(url + "/cleanings", poolid) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Cleanings].cleanings.length shouldEqual 1
      }
    }
  }
}