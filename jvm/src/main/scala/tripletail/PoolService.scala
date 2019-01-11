package tripletail

import akka.http.scaladsl.marshalling.ToResponseMarshallable
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.server.Directives._
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._

object PoolService {
  val index = path("") {
    getFromResource("public/index.html")
  }
  val resources = get {
    getFromResourceDirectory("public")
  }
  val postOwner = path("owners") {
    post {
      entity(as[Owner]) { owner =>
        onSuccess(PoolRepository.postOwner(owner)) {
          complete( OK )
        }
      }
    }
  }
  val getOwner = path("owners" / Segment) { license =>
    get {
      onSuccess(PoolRepository.getOwner(license)) {
        case Some(owner) => complete( ToResponseMarshallable[Owner](owner) )
        case None => complete( NotFound )
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    postOwner ~ getOwner
  }
  val routes = index ~ resources ~ api
}