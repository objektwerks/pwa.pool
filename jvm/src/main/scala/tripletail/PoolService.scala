package tripletail

import java.time.LocalDate
import java.util.UUID

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
  val signup = path("signup" / Segment ) { email =>
    get {
      val owner = Owner(UUID.randomUUID.toString, LocalDate.now, email)
      onSuccess(PoolRepository.signup(owner)) {
        complete( ToResponseMarshallable[Owner](owner) )
      }
    }
  }
  val signin = path("signin" / Segment) { license =>
    get {
      onSuccess(PoolRepository.signin(license)) {
        case Some(owner) => complete( ToResponseMarshallable[Owner](owner) )
        case None => complete( NotFound )
      }
    }
  }
  val api = pathPrefix("api" / "v1" / "tripletail") {
    signup ~ signin
  }
  val routes = index ~ resources ~ api
}