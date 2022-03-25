package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom.raw.HTMLButtonElement
import org.scalajs.dom.{console, document}

import pool.container.Container
import pool.dialog.{AccountDialog, LoginDialog, RegisterDialog}
import pool.menu.HomeMenu
import pool.view.{PoolsView, PoolView}

object Context {
  def apply(publicUrl: String, apiUrl: String): Context = new Context(publicUrl, apiUrl)

  def log(message: Any): Unit = console.log(message)
}

class Context(publicUrl: String, apiUrl: String) {
  log(s"[context] public url: $publicUrl api url: $apiUrl")

  val email = Var("")
  val pin = Var("")
  val account = Var(Account.emptyAccount)
  val pools = Var(Seq.empty[Pool])
  val selectedPool = Var[Pool](Pool())

  val content = Container(
    HomeMenu(
      this,
      RegisterDialog(this),
      LoginDialog(this),
      AccountDialog(this)
    ),
    PoolsView(this),
    PoolView(this)
  )

  val registerUrl = s"$publicUrl/register"
  val loginUrl = s"$publicUrl/login"
  val deactivateUrl = s"$publicUrl/deactivate"
  val reactivateUrl = s"$publicUrl/reactivate"
  val poolsUrl = s"$apiUrl/pools"
  val poolsAddUrl = s"$apiUrl/pools/add"
  val poolsUpdateUrl = s"$apiUrl/pools/update"

  def log(message: Any): Unit = Context.log(message)

  def hide(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")

  def show(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def hideAndShow(hideId: String, showId: String): Unit = {
    hide(hideId)
    show(showId)
  }

  def disable(id: String): Unit = setDisabled(id, isDisabled = true)

  def enable(id: String): Unit = setDisabled(id, isDisabled = false)

  def disableAndEnable(disableId: String, enableId: String): Unit = {
    disable(disableId)
    enable(enableId)
  }

  private def setDisabled(id: String, isDisabled: Boolean): Unit = {
    document.getElementById(id) match {
      case button: HTMLButtonElement => button.disabled = isDisabled
      case _ => log(s"Element -> id: $id isDisabled: $isDisabled")
    }
  }
}