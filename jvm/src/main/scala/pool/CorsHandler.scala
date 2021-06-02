package pool

import akka.http.scaladsl.model.HttpMethods._
import akka.http.scaladsl.model.headers._
import akka.http.scaladsl.model.{HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.{Directive0, Route}

trait CorsHandler {
  val corsResponseHeaders = List(
    `Access-Control-Allow-Origin`.*,
    `Access-Control-Allow-Credentials`(true),
    `Access-Control-Allow-Headers`("Authorization", "Content-Type", "X-Requested-With")
  )

  def corsHandler(route: Route): Route = addAccessControlHeaders { preflightRequestHandler ~ route }

  def addAccessControlHeaders: Directive0 = respondWithHeaders( corsResponseHeaders )

  def preflightRequestHandler: Route = options {
    complete( HttpResponse( StatusCodes.OK ).withHeaders( `Access-Control-Allow-Methods`(OPTIONS, GET, POST, PUT, DELETE) ) )
  }

  def addCORSHeaders(response: HttpResponse): HttpResponse = response.withHeaders( corsResponseHeaders )
}