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

  val url = routes.url
  var licensee: Licensee = _
  var licenseHeader: RawHeader = _
  var poolId: PoolId = _
  var timerId: TimerId = _
  var heaterId: HeaterId = _

  "signup" should {
    "post to signedup" in {
      Post("/signup", SignUp(email = "test@test.com")) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        licensee = responseAs[SignedUp].licensee
        licenseHeader = RawHeader(Licensee.licenseHeaderKey, licensee.license)
        licensee.license.nonEmpty shouldBe true
        licensee.email.nonEmpty shouldBe true
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
        poolId = PoolId(pool.id)
        pool.id should be > 0
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
      var surface = Surface(poolId = poolId.id, installed = localDateToInt(1991, 3, 13), kind = "concrete")
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
      Post(url + "/surfaces", poolId) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Surfaces].surfaces.length shouldEqual 1
      }
    }
  }
}