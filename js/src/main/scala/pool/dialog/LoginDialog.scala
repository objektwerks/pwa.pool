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
        Label("Email Address"),
        Text.email(context.email)
      ),
      Field(
        Label("Pin"),
        Text.text(context.pin).amend {
          minLength(9)
          maxLength(9)
        }
      ),
      MenuButtonBar(
        MenuButton("Cancel").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton("Login").amend {
          onClick --> { _ =>
            val command = Login(context.email.now(), context.pin.now())
            val response = CommandProxy.post(context.loginUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        }
      )
    )
}