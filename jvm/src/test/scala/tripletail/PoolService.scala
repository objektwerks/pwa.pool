package tripletail

object PoolService {
  import akka.http.scaladsl.server.Directives._

  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val routes = index ~ resources
}