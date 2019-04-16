package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

object PoolApp {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val conf = ConfigFactory.load("app.conf")
    val store = PoolStore(conf)
    val cache = LicenseeCache(store)
    val routes = PoolRoutes(store, cache)
    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")
    Http().bindAndHandle(routes.routes, host, port)

    val logger = LoggerFactory.getLogger(PoolApp.getClass)
    logger.info(s"Pool app started at http://$host:$port")
  }
}