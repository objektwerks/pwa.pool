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

  val url = "/api/v1/tripletail/"
  val contentTypeHeader = RawHeader("content-type", "application/json; charset=utf-8")
  val acceptHeader = RawHeader("accept", "application/json")

  "signup" should {
    "post signup" in {
      Post(url + "signup", SignUp("objektwerks@runbox.com")) ~> routes.routes ~> check {
        status shouldBe StatusCodes.OK
      }
    }
  }
}