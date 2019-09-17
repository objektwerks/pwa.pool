package tripletail

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

  import de.heikoseeberger.akkahttpupickle.UpickleSupport._
  import DateTime._
  import StatusCodes._
  import Serialization._

  val url = routes.url
  var licensee: Licensee = _
  var licenseHeader: RawHeader = _
  var pool: Pool = _

  "signup" should {
    "post to secure" in {
      val email = "objektwerks@runbox.com"
      Post("/signup", Signup(email)) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        licensee = responseAs[Secure].licensee
        licenseHeader = RawHeader(Licensee.licenseHeaderKey, licensee.license)
        licensee.license.nonEmpty shouldBe true
      }
    }
  }

  "signin" should {
    "post to secure" in {
      Post(url + "/signin", Signin(licensee.license, licensee.email)) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Secure].licensee shouldEqual licensee
      }
    }
  }

  "pools / add / update" should {
    "post to generated, updated, pools" in {
      pool = Pool(license = licensee.license, built = localDateToInt(1991, 3, 13), lat = 26.862631, lon = -82.288834, volume = 10000)
      Post(url + "/pools/add", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        pool = pool.copy(id = responseAs[Generated].id)
        pool.id should be > 0
      }
      pool = pool.copy(volume = 9000)
      Post(url + "/pools/update", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        responseAs[Updated].count shouldEqual 1
      }
      Post(url + "/pools", licensee) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe OK
        val pools = responseAs[Pools].pools
        pools.length shouldEqual 1
      }
    }
  }
}