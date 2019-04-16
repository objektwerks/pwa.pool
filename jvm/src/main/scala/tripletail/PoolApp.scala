package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object PoolApp {
  def main(args: Array[String]): Unit = {
    val conf = ConfigFactory.load("app.conf")

    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val store = PoolStore(conf)
    val cache = LicenseeCache(store)
    val routes = PoolRoutes(store, cache)
    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")

    Http().bindAndHandle(routes.routes, host, port)
    println(s"Pool app started at http://$host:$port")
  }
}