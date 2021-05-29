package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.experimental.serviceworkers._
import org.scalajs.dom.ext.KeyCode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel
import scala.util.{Failure, Success}

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  println(s"public url: $publicUrl")
  println(s"api url: $apiUrl")

  val serverProxy = ServerProxy()
  val email = Var("")
  val pin = Var("")
  val onEnterPress = onKeyPress.filter(_.keyCode == KeyCode.Enter)

  registerServiceWorker()
  var root = render(document.getElementById("client"), renderHome)


  def renderHome: Div =
    div(
      renderNavigation,
      renderNow
    )

  def renderCenter(center: Div): Unit = {
    root.unmount()
    root = render(document.getElementById("client"), center)
  }


  def renderNavigation: Div =
    div(
      idAttr("navigation"),
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), onClick --> (_ => renderCenter( renderRegister) ), cls("w3-bar-item w3-button"), "Register" ),
      a( href("#"), onClick --> (_ => renderCenter( renderLogin) ), cls("w3-bar-item w3-button"), "Login" )
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
      div(cls("w3-container"), paddingTop("3px"), paddingBottom("3px"),
        div(cls("w3-row"),
          div(cls("w3-col"), width("15%"),
            label(cls("w3-left-align w3-text-indigo"), "Email:")),
          div(cls("w3-col"), width("85%"),
            input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
              inContext { input =>
                onEnterPress.mapTo(input.ref.value).filter(_.nonEmpty) --> { value =>
                  email.set(value)
                  input.ref.value = ""
                }
              }
            )
          )
        )
      )
    )

  def renderLogin: Div =
    div(
      div(cls("w3-container"), paddingTop("3px"), paddingBottom("3px"),
        div(cls("w3-row"),
          div(cls("w3-col"), width("15%"),
            label(cls("w3-left-align w3-text-indigo"), "Email:")),
          div(cls("w3-col"), width("85%"),
            input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
              inContext { input =>
                onEnterPress.mapTo(input.ref.value).filter(_.nonEmpty) --> { value =>
                  email.set(value)
                  input.ref.value = ""
                }
              }
            )
          )
        ),
        div(cls("w3-row"),
          div(cls("w3-col"), width("15%"),
            label(cls("w3-left-align w3-text-indigo"), "PIN:")),
          div(cls("w3-col"), width("85%"),
            input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
              inContext { input =>
                onEnterPress.mapTo(input.ref.value).filter(_.nonEmpty) --> { value =>
                  pin.set(value)
                  input.ref.value = ""
                }
              }
            )
          )
        )
      )
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