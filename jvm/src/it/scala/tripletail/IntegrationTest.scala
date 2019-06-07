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

  "signup" should {
    "post to signedup" in {
      val email = "objektwerks@runbox.com"
      Post("/signup", SignUp(email)) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        val signedUp = responseAs[SignedUp]
        licensee = signedUp.licensee
        licenseHeader = RawHeader(Licensee.licenseHeaderKey, signedUp.licensee.license)
        signedUp.licensee.license.nonEmpty shouldBe true
      }
    }
  }

  "signin" should {
    "post to signedin" in {
      Post("/signin", SignIn(licensee.license, licensee.email)) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[SignedIn].licensee shouldEqual licensee
      }
    }
  }

  "pools / add / update" should {
    "post to id, count, sequence" in {
      pool = Pool(license = licensee.license, built = "1991-03-13", lat = 26.862631, lon = -82.288834, volume = 10000)
      Post(url + "/pools/add", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        val id = responseAs[Id].id
        pool = pool.copy(id = id)
        id should be > 0
      }
      pool = pool.copy(volume = 9000)
      Post(url + "/pools/update", pool) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        responseAs[Count].value shouldEqual 1
      }
      Post(url + "/pools", licensee) ~> addHeader(licenseHeader) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
        val pools = responseAs[Sequence].entities
        pools.length shouldEqual 1
        pools.asInstanceOf[List[Pool]].foreach(println)
      }
    }
  }
}