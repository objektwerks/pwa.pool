package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.model.headers.RawHeader
import akka.http.scaladsl.testkit.ScalatestRouteTest
import com.typesafe.config.ConfigFactory
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport
import io.circe.generic.auto._
import org.scalatest.{Matchers, WordSpec}
import org.slf4j.LoggerFactory

class IntegrationTest extends WordSpec with Matchers with ScalatestRouteTest with FailFastCirceSupport {
  val logger = LoggerFactory.getLogger(this.getClass)
  val actorRefFactory = ActorSystem.create(this.getClass.getSimpleName)
  implicit val dispatcher = system.dispatcher

  val conf = ConfigFactory.load("it.test.conf")
  val store = PoolStore(conf)
  val cache = LicenseeCache(store)
  val routes = PoolRoutes(store, cache)
  val host = conf.getString("server.host")
  val port = conf.getInt("server.port")
  val server = Http().bindAndHandle(routes.routes, host, port)
  server.map { server => logger.info(s"*** Pool app integration test host: ${server.localAddress.toString}") }

  val url = "/api/v1/tripletail"
  var licensee: Licensee = _
  var licenseHeader: RawHeader = _
  var pool: Pool = _

  import StatusCodes._
  import DateTime._

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