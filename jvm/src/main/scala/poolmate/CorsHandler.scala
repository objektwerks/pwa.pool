package poolmate

import cask.main.Main
import cask.internal.DispatchTrie
import cask.util.Logger
import cask.main.Routes
import cask.router.EndpointMetadata
import cask.router.Decorator
import cask.model.Response
import cask.router.Result
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import io.undertow.Undertow
import io.undertow.server.HttpServerExchange
import io.undertow.server.handlers.BlockingHandler
import io.undertow.util.Headers
import io.undertow.util.HttpString

import java.util.concurrent.TimeUnit

import scala.concurrent.duration._
import scala.io.StdIn
import scala.jdk.CollectionConverters.*

given log: Logger = new Logger.Console()

object CorsHandler:
  val accessControlAllowOrigin = new HttpString("Access-Control-Allow-Origin")
  val accessControlAllowCredentials = new HttpString("Access-Control-Allow-Credentials")
  val acccessControlAllowHeaders = new HttpString("Access-Control-Allow-Headers")
  val accessControlAllowMethods = new HttpString("Access-Control-Allow-Methods")

  val origin = "*"
  val accepted = "true"
  val headers = Set("Authorization", "Content-Type", "X-Requested-With").asJava
  val methods = Set("POST", "GET", "PUT", "DELETE", "PATCH", "HEAD", "OPTIONS").asJava

class CorsHandler(dispatchTrie: DispatchTrie[Map[String, (Routes, EndpointMetadata[_])]],
                  mainDecorators: Seq[Decorator[_, _, _]],
                  debugMode: Boolean,
                  handleNotFound: () => Response.Raw,
                  handleMethodNotAllowed: () => Response.Raw,
                  handleError: (Routes, EndpointMetadata[_], Result.Error) => Response.Raw)
  extends Main.DefaultHandler(dispatchTrie,
                              mainDecorators,
                              debugMode,
                              handleNotFound,
                              handleMethodNotAllowed,
                              handleError)(using log: Logger):
  override def handleRequest(exchange: HttpServerExchange): Unit =
    import CorsHandler.*
    exchange.getResponseHeaders
      .put(accessControlAllowOrigin, origin)
      .put(accessControlAllowCredentials, accepted)
      .putAll(acccessControlAllowHeaders, headers)
      .putAll(accessControlAllowMethods, methods)
    super.handleRequest(exchange)