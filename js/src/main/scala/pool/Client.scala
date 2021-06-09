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
  val navigation = Navigation.render(context, model)
  val entities = Entities.render(context, model)
  val content = Content.render(navigation, entities)
  render(document.getElementById("content"), content)
}