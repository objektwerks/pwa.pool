package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.ext.KeyCode

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()

  val serverProxy = ServerProxy()
  val eventObserver = EventObserver()
  val commandObserver = CommandObserver(apiUrl)

  val onKeyUp = onKeyPress.filter(_.keyCode == KeyCode.Up)

  var root = render(document.getElementById("client"), renderHome)

  val email = Var("")
  val pin = Var(0)

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
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), onClick --> (_ => renderCenter( renderRegister) ), cls("w3-bar-item w3-button"), "Register" ),
      a( href("#"), onClick --> (_ => renderCenter( renderLogin) ), cls("w3-bar-item w3-button"), "Login" )
    )

  def renderNow: Div = {
    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    div(
      label(
        cls("w3-text-indigo"),
        fontSize("12px"),
        child.text <-- datetimeVar
      )
    )
  }

  def renderRegister: Div =
    div(cls("w3-container"), paddingTop("3px"), paddingBottom("3px"),
      div(cls("w3-row"),
        div(cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div(cls("w3-col"), width("85%"),
          input(idAttr("register-email"), cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
            inContext { input =>
              onKeyUp.mapTo(input.ref.value).filter(_.nonEmpty) --> { value =>
                email.set(value)
              }
            }
          )
        )
      ),
      div(cls("w3-row"),
        button( onClick.mapTo(SignUp(email.now())) --> commandObserver, cls("w3-btn w3-indigo"), "Register" )
      )
    )

  def renderLogin: Div =
    div(cls("w3-container"), paddingTop("3px"), paddingBottom("3px"),
      div(cls("w3-row"),
        div(cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div(cls("w3-col"), width("85%"),
          input( idAttr("login-email"), cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
            inContext { input =>
              onKeyUp.mapTo(input.ref.value).filter(_.nonEmpty) --> { value =>
                email.set(value)
              }
            }
          )
        )
      ),
      div(cls("w3-row"),
        div(cls("w3-col"), width("15%"),
          label(cls("w3-left-align w3-text-indigo"), "PIN:")
        ),
        div(cls("w3-col"), width("85%"),
          input( idAttr("login-pin"), cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
            inContext { input =>
              onKeyUp.mapTo(input.ref.value).filter(_.toIntOption.nonEmpty) --> { value =>
                pin.set(value.toInt)
              }
            }
          )
        )
      ),
      div(cls("w3-row"),
        button( onClick.mapTo(SignIn(email.now(), pin.now())) --> commandObserver, cls("w3-btn w3-indigo"), "Login" )
      )
    )
}