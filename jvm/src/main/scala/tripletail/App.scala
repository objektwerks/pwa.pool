package tripletail

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object App {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(App.getClass)
    val conf = ConfigFactory.load("app.conf")
    implicit val system = ActorSystem.create(conf.getString("server.name"), conf.getConfig("akka"))
    implicit val materializer = ActorMaterializer()
    implicit val dispatcher = system.dispatcher

    val store = Store(conf)
    val cache = LicenseeCache(store)
    val emailer = system.actorOf(Props(classOf[Emailer], conf), name = "emailer")
    val router = Router(store, cache, emailer)
    val host = conf.getString("server.host")
    val port = conf.getInt("server.port")
    Http()
      .bindAndHandle(router.routes, host, port)
      .map { server =>
        logger.info(s"*** App host: ${server.localAddress.toString}")
      }

    sys.addShutdownHook {
      logger.info("*** App shutting down...")
      system.terminate()
      Await.result(system.whenTerminated, 30.seconds)
      logger.info("*** App shutdown.")
    }
    ()
  }
}