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
         onClick --> (_ => renderRoot( Login.render(context, model).amend( display("block") ) ) ),
         "Login" )
    )
}