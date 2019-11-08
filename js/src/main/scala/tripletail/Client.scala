package tripletail

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("Client")
object Client {
  private val serverUrl = "http://127.0.0.1:7979"
  private val apiUrl = "http://127.0.0.1:7979/api/v1/tripletail"

  @JSExport
  def init(): Unit = {
    registerServiceWorker()
    val licenseeStore = LicenseeStore()
    val serverProxy = ServerProxy()
    val canvas = Canvas(licenseeStore, serverProxy, serverUrl, apiUrl)
    canvas.init()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker
      .register("sw-opt.js")
      .toFuture
      .onComplete {
        case Success(registration) =>
          println("registerServiceWorker: registered service worker")
          registration.update()
        case Failure(error) => println(s"registerServiceWorker: service worker registration failed > ${error.printStackTrace()}")
      }
  }
}