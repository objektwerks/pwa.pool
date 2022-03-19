package poolmate

import cask.main.Main
import com.typesafe.config.ConfigFactory
import com.typesafe.scalalogging.LazyLogging

import io.undertow.Undertow
import io.undertow.server.handlers.BlockingHandler

import java.util.concurrent.TimeUnit

import scala.concurrent.duration._
import scala.io.StdIn

object Server extends Main with LazyLogging:
  val conf = ConfigFactory.load("server.conf")
  
  val store = Store(conf, Store.cache(minSize = 4, maxSize = 10, expireAfter = 24.hour))
  val service = Service(store)
  val authorizer = Authorizer(service)
  val handler = Handler(service)
  val validator = Validator()
  val dispatcher = Dispatcher(authorizer, validator, handler)

  val nowRouter = NowRouter()
  val commandEventRouter = CommandEventRouter(dispatcher, store)

  override val allRoutes = Seq(nowRouter, commandEventRouter)
  
  override def host: String = conf.getString("host")

  override def port: Int = conf.getInt("port")

  override def defaultHandler: BlockingHandler =
    new BlockingHandler( CorsHandler(dispatchTrie,
                                     mainDecorators,
                                     debugMode = false,
                                     handleNotFound,
                                     handleMethodNotAllowed,
                                     handleEndpointError) )

  override def main(args: Array[String]): Unit =
    Main.silenceJboss()    
    val server = Undertow.builder
      .addHttpListener(port, host)
      .setHandler(defaultHandler)
      .build

    server.start()
    val started = s"*** Server started at http://$host:$port/\nPress RETURN to stop..."
    println(started)
    logger.info(started)

    StdIn.readLine()
    server.stop()
    val stopped = s"*** Server stopped!"
    println(stopped)
    logger.info(stopped)