package pool

import com.raquo.laminar.api.L._
import org.scalajs.dom._

import pool.dialog.{AccountDialog, LoginDialog, RegisterDialog}
import pool.menu.HomeMenu
import pool.proxy.NowProxy
import pool.view.PoolsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  ServiceWorker.register()
  NowProxy.post(s"$publicUrl/now").foreach(println)

  val context = Context(publicUrl, apiUrl)
  val container = Container(
    HomeMenu(
      context,
      RegisterDialog(context),
      LoginDialog(context),
      AccountDialog(context)
    ),
    PoolsView(context)
  )
  render(document.getElementById("content"), container)
}