package pool.dialog

import com.raquo.laminar.api.L._
import pool._
import pool.Validators._
import pool.component._
import pool.handler.EventHandler
import pool.menu.{HomeMenu, MenuButton, MenuButtonBar}
import pool.proxy.CommandProxy
import pool.text.{Errors, Header, Label, Text}

object LoginDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  val pin = Var(0)

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case loggedin: LoggedIn =>
        AccountDialog.account.set(loggedin.account)
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(HomeMenu.loginMenuItemId)
        context.show(HomeMenu.accountMenuItemId)
        if (loggedin.account.isActivated) context.show(HomeMenu.poolsMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Login"),
      Errors(errors),
      Field(
        Label(column = "15%", name = "Pin:"),
        Text(column = "85%", Text.field(typeOf = "number").amend {
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> pin
        })
      ),
      MenuButtonBar(
        MenuButton(name = "Login").amend {
          onClick --> { _ =>
            val command = Login(pin.now())
            val response = CommandProxy.post(context.loginUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        },
        MenuButton(name = "Cancel").amend {
          onClick --> { _ => context.hide(id) }
        }
      )
    )
}