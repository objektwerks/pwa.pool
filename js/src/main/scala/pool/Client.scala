package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import pool.dialog.{LoginDialog, RegisterDialog}
import pool.menu.RegisterLoginMenu
import pool.view.PoolsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()
  ServerProxy.post(s"$publicUrl/now").foreach(println)

  val context = Context( Urls( publicUrl, apiUrl ), Model() )
  val content = Content(
    RegisterLoginMenu( RegisterDialog(context), LoginDialog(context) ),
    PoolsView(context)
  )
  render(document.getElementById("content"), content)
}