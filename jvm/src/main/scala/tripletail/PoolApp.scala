package tripletail

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory

object PoolApp {
  def main(args: Array[String]): Unit = {
    implicit val system = ActorSystem()
    implicit val materializer = ActorMaterializer()

    val conf = ConfigFactory.load()
    val host = conf.getString("app.host")
    val port = conf.getInt("app.port")
    val store = PoolStore()
    val cache = LicenseeCache(store)
    val routes = PoolRoutes(store, cache)

    Http().bindAndHandle(routes.routes, host, port)
    println(s"Pool app started at http://$host:$port")
  }
}