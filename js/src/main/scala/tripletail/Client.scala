package tripletail

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("Client")
object Client {
  @JSExport
  def init(): Unit = {
    registerServiceWorker()
    val serverProxy = ServerProxy("http://127.0.0.1:7979/api/v1/tripletail")
    val canvas = Canvas(serverProxy)
    canvas.init()
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