package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._

import scala.annotation.nowarn
import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success}

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  println(s"public url: $publicUrl")
  println(s"api url: $apiUrl")

  registerServiceWorker()
  render(document.getElementById("client"), renderHome)

  val serverProxy = ServerProxy()

  def renderHome: Div =
    div(
      renderNavigation,
      renderNow
    )

  @nowarn def renderNavigation: Div =
    div(
      idAttr("navigation"),
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), onClick --> (_ => renderRegister), cls("w3-bar-item w3-button"), "Register" ),
      a( href("#"), onClick --> (_ => renderLogin), cls("w3-bar-item w3-button"), "Login" )
    )

  def renderNow: Div = {
    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    div(
      idAttr("now"),
      label(
        cls("w3-text-indigo"),
        fontSize("12px"),
        child.text <-- datetimeVar
      )
    )
  }

  def renderRegister: Div =
    div(
      idAttr("register"),
      cls("w3-container"),
      label("Email Address:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") )
    )

  def renderLogin: Div =
    div(
      idAttr("login"),
      cls("w3-container"),
      label("Email Address:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") ),
      label("PIN:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") )
    )

  def registerServiceWorker(): Unit =
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