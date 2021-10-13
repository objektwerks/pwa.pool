package pool.dialog

import com.raquo.laminar.api.L._

import pool.Validators._
import pool._
import pool.container._
import pool.handler.EventHandler
import pool.menu.{HomeMenu, MenuButton, MenuButtonBar}
import pool.proxy.CommandProxy
import pool.text.{Errors, Header, Label, Text}

object LoginDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit =
    event match {
      case loggedin: LoggedIn =>
        context.account.set(loggedin.account)
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(HomeMenu.loginMenuItemId)
        context.show(HomeMenu.accountMenuItemId)
        if (loggedin.account.isActivated) context.show(HomeMenu.poolsMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Login"),
      Errors(errors),
      Field(
        Label(name = "Pin:"),
        Text.integer(integer = context.pin)
      ),
      MenuButtonBar(
        MenuButton(name = "Cancel").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton(name = "Login").amend {
          onClick --> { _ =>
            val command = Login(context.pin.now())
            val response = CommandProxy.post(context.loginUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        }
      )
    )
}