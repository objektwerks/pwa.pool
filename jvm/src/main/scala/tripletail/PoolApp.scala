package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object PoolApp extends App {
  val logger = LoggerFactory.getLogger(PoolApp.getClass)
  implicit val system = ActorSystem()
  implicit val materializer = ActorMaterializer()

  val conf = ConfigFactory.load("app.conf")
  val store = PoolStore(conf)(system.dispatcher)
  val cache = LicenseeCache(store)
  val routes = PoolRoutes(store, cache)
  val host = conf.getString("app.host")
  val port = conf.getInt("app.port")
  Http().bindAndHandle(routes.routes, host, port)

  logger.info(s"Pool app started at http://$host:$port")

  sys.addShutdownHook {
    logger.info("Pool app shutting down...")
    system.terminate()
    Await.result(system.whenTerminated, 30.seconds)
    logger.info("Pool app shutdown.")
  }
}