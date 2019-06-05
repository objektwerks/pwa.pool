package tripletail

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("PoolClient")
object PoolClient {
  @JSExport
  def init(): Unit = {
    registerServiceWorker()
    val poolRestClient = PoolServerClient("http://127.0.0.1:7979/api/v1/tripletail")
    val poolModelView = PoolModelView(poolRestClient)
    poolModelView.init()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker
      .register("sw-fastopt.js")
      .toFuture
      .onComplete {
        case Success(registration) =>
          println("registerServiceWorker: registered service worker")
          registration.update()
        case Failure(error) => println(s"registerServiceWorker: service worker registration failed > ${error.printStackTrace()}")
      }
  }
}