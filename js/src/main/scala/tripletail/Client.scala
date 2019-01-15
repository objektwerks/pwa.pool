package tripletail

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js.annotation.{JSExport, JSExportTopLevel}
import scala.util.{Failure, Success}

@JSExportTopLevel("PoolClient")
object Client {
  @JSExport
  def init(): Unit = {
    version()
    registerServiceWorker()
    val poolRestClient = RestClient(url = "http://127.0.0.1:7979/api/v1/tripletail")
    val poolModelView = ModelView(poolRestClient)
    poolModelView.init()
  }

  def registerServiceWorker(): Unit = {
    toServiceWorkerNavigator(window.navigator)
      .serviceWorker
      .register("/sw-opt.js")
      .toFuture
      .onComplete {
        case Success(registration) =>
          println("registerServiceWorker: registered service worker")
          registration.update()
        case Failure(error) => println(s"registerServiceWorker: service worker registration failed > ${error.printStackTrace()}")
      }
  }

  def version(): Unit = {
    val div = document.getElementById("version")
    val p = document.createElement("p")
    val text = document.createTextNode("V3")
    val node = p.appendChild(text)
    div.appendChild(node)
    ()
  }
}