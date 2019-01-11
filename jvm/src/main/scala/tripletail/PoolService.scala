package tripletail

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes
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
  val getOwner = path("owners" / Segment) { license =>
    get {
      onSuccess(PoolRepository.getOwner(license)) {
        case Some(owner) => complete( ToResponseMarshallable[Owner](owner) )
        case None => complete( StatusCodes.NotFound )
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    getOwner
  }
  val routes = index ~ resources
}