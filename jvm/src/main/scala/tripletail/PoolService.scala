package tripletail

import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

import scala.concurrent.ExecutionContext

object PoolService {
  implicit val ec = ExecutionContext.Implicits.global

  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val getOwner = path("owners" / IntNumber) { id =>
    get {
      complete {
        PoolRepository.getOwner(id)
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    getOwner
  }
  val routes = index ~ resources
}