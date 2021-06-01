package pool

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers.{`Access-Control-Allow-Credentials`, `Access-Control-Allow-Headers`, `Access-Control-Allow-Methods`, `Access-Control-Allow-Origin`}
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}

trait CorsHandler {
  val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`.apply("*")
  )

  def corsHandler(route: Route): Route = addAccessControlHeaders { preflightRequestHandler ~ route }

  def addAccessControlHeaders: Directive0 = respondWithHeaders(corsResponseHeaders)

  def preflightRequestHandler: Route = options {
    complete( HttpResponse(StatusCodes.OK )
      .withHeaders(`Access-Control-Allow-Methods`(GET, POST)))
  }
}