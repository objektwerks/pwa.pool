package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("Client")
object Client {
  @JSExport
  def init(apiUrl: String): Unit = {
    registerServiceWorker()
    render(document.getElementById("container"), Container(ServerProxy(), apiUrl))
    ()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker
      .register("js/main.js")
      .toFuture
      .onComplete {
        case Success(registration) =>
          println("registerServiceWorker: registered service worker")
          registration.update()
        case Failure(error) => println(s"registerServiceWorker: service worker registration failed > ${error.printStackTrace()}")
      }
  }
}