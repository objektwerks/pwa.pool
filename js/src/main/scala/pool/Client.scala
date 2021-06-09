package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()
  ServerProxy.post(s"$publicUrl/now").foreach(println)

  val context = Context(publicUrl, apiUrl)
  val model = Model()
  val content = document.getElementById("content")
  render(content, renderNavigation)

  def renderRoot(div: Div): Unit = content.innerHTML = div.ref.innerHTML

  def renderNavigation: Div =
    div( cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), cls("w3-bar-item w3-button"),
         onClick --> (_ => renderRoot( Register.render(context, model).amend( display("block") ) ) ),
         "Register" ),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => renderRoot( renderLogin.amend( display("block") ) ) ),
         "Login" )
    )

  def renderLogin: Div =
    div( idAttr("login"), cls("w3-modal"),
      div( cls("w3-modal-content w3-card w3-animate-left"),
        div( cls("w3-row"),
          div( cls("w3-col"), width("15%"),
            label( cls("w3-left-align w3-text-indigo"), "Email:" )
          ),
          div( cls("w3-col"), width("85%"),
            input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
              onChange.mapToValue.filter(_.nonEmpty) --> model.email
            )
          )
        ),
        div( cls("w3-row"),
          div( cls("w3-col"), width("15%"),
            label( cls("w3-left-align w3-text-indigo"), "Pin:" )
          ),
          div( cls("w3-col"), width("85%"),
            input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
              onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> model.pin
            )
          )
        ),
        div( cls("w3-row w3-padding-16"),
          button( cls("w3-btn w3-text-indigo"),
            onClick.mapTo { 
              document.getElementById("login").setAttribute("style", "display: none")
              SignIn(model.email.now(), model.pin.now()) 
            } --> context.commandObserver,
            "Login"
          ),
          button( cls("w3-btn w3-text-indigo"),
            onClick --> (_ => document.getElementById("login").setAttribute("style", "display: none") ),
            "Cancel"
          )
        )
      )
    )
}