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
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()
    implicit val dispatcher = system.dispatcher

    val conf = ConfigFactory.load("app.conf")
    val store = Store(conf)
    val cache = LicenseeCache(store)
    val emailSender = system.actorOf(Props(classOf[EmailSender], conf), name = "emailSender")
    val router = Router(store, cache, emailSender)
    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")
    Http()
      .bindAndHandle(router.routes, host, port)
      .map { server =>
        logger.info(s"*** App host: ${server.localAddress.toString}")
      }

    val emailReceiver = system.actorOf(Props(classOf[EmailReceiver], conf), name = "emailReceiver")
    emailReceiver ! ReceiveEmail

    sys.addShutdownHook {
      logger.info("*** App shutting down...")
      system.terminate()
      Await.result(system.whenTerminated, 30.seconds)
      logger.info("*** App shutdown.")
    }
    ()
  }
}