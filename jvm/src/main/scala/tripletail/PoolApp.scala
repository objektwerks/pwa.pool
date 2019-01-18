package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

import scala.io.StdIn

object PoolApp {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materlizer = ActorMaterializer()
    implicit val executor = system.dispatcher
    val conf = ConfigFactory.load()

    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")
    val server = Http().bindAndHandle(PoolRoutes.routes, host, port)
    println(s"Pool app started at http://$host:$port/\nPress RETURN to stop...")

    StdIn.readLine()
    server.flatMap(_.unbind()).onComplete { _ =>
      system.terminate()
      println("Pool app stopped.")
    }
  }
}