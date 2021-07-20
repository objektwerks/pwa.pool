package pool

import akka.actor.ActorSystem
import akka.http.scaladsl.Http

import com.typesafe.config.ConfigFactory

import org.slf4j.LoggerFactory

import scala.concurrent.Await
import scala.concurrent.duration._
import scala.io.StdIn

object Server {
  def main(args: Array[String]): Unit = {
    val logger = LoggerFactory.getLogger(Server.getClass)
    val conf = ConfigFactory.load("server.conf")
    implicit val system = ActorSystem.create(conf.getString("server.name"), conf.getConfig("akka"))
    implicit val dispatcher = system.dispatcher

    val store = Store(conf)
    val cache = AccountCache(store)
    val emailer = Emailer(conf)
    val router = Router(store, cache, emailer, logger)
    val host = conf.getString("server.host")
    val port = conf.getInt("server.port")
    val server = Http()
      .newServerAt(host, port)
      .bindFlow(router.routes)
      .map { server =>
        logger.info(s"*** Server host: ${server.localAddress.toString}")
        server
      }

    println(s"*** Server started at: $host:$port Press return to shutdown.")

    StdIn.readLine()
    server
      .flatMap(_.unbind())
      .onComplete { _ =>
        logger.info("*** Server shutting down...")
        system.terminate()
        Await.result(system.whenTerminated, 30.seconds)
        logger.info("*** Server shutdown.")
      }
    ()
  }
}