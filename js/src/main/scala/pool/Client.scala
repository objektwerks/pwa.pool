package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()

  val context = Context(apiUrl)
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
    ServerProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    div(
      label( cls("w3-text-indigo"), fontSize("12px"), child.text <-- datetimeVar )
    )
  }

  def renderRegister: Div =
    div( cls("w3-container w3-padding-16"),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
            onChange.mapToValue.filter(_.nonEmpty) --> email
          )
        )
      ),
      div( cls("w3-row w3-padding-16"),
        button( onClick.mapTo( SignUp(email.now()) ) --> context.commandObserver, cls("w3-btn w3-text-indigo"), "Register" )
      )
    )

  def renderLogin: Div =
    div( cls("w3-container w3-padding-16"),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Email:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
            onChange.mapToValue.filter(_.nonEmpty) --> email
          )
        )
      ),
      div( cls("w3-row"),
        div( cls("w3-col"), width("15%"),
          label( cls("w3-left-align w3-text-indigo"), "Pin:" )
        ),
        div( cls("w3-col"), width("85%"),
          input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
            onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> pin
          )
        )
      ),
      div( cls("w3-row w3-padding-16"),
        button( onClick.mapTo( SignIn(email.now(), pin.now()) ) --> context.commandObserver, cls("w3-btn w3-text-indigo"), "Login" )
      )
    )
}