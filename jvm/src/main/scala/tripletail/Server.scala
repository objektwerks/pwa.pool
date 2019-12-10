package tripletail

import akka.actor.{ActorSystem, Props}
import akka.http.scaladsl.Http
import com.typesafe.config.ConfigFactory
import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._

object Server {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(Server.getClass)
    val conf = ConfigFactory.load("server.conf")
    implicit val system = ActorSystem.create(conf.getString("server.name"), conf.getConfig("akka"))
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
        logger.info(s"*** Server host: ${server.localAddress.toString}")
      }

    sys.addShutdownHook {
      logger.info("*** Server shutting down...")
      system.terminate()
      Await.result(system.whenTerminated, 30.seconds)
      logger.info("*** Server shutdown.")
    }
    ()
  }
}