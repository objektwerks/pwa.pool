package pool

import com.raquo.laminar.api.L._
import org.scalajs.dom._
import pool.container.Container
import pool.dialog.{AccountDialog, LoginDialog, PoolDialog, RegisterDialog}
import pool.menu.HomeMenu
import pool.proxy.NowProxy
import pool.view.PoolsView

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  val container = Container()
  val context = Context(container, publicUrl, apiUrl)
  context.log(s"[context] public url: $publicUrl api url: $apiUrl")

  container.amend {
    HomeMenu(
      context,
      RegisterDialog(context),
      LoginDialog(context),
      AccountDialog(context)
    )
    PoolsView(context)
    PoolDialog(context)
  }

  ServiceWorker.register()

  NowProxy.post(s"$publicUrl/now").foreach(now => context.log(s"[now] $now"))

  render(document.getElementById("content"), container)
}