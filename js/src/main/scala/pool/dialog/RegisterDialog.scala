package pool.dialog

import com.raquo.laminar.api.L._

import pool._
import pool.container._
import pool.handler.EventHandler
import pool.menu.{HomeMenu, MenuButton, MenuButtonBar}
import pool.proxy.CommandProxy
import pool.text._

object RegisterDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit =
    event match {
      case Registering(_) =>
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Register"),
      Note("Check your email for new account details, provided your given email address is valid."),
      Errors(errors),
      Field(
        Label("Email Address"),
        Text.email(context.email)
      ),
      MenuButtonBar(
        MenuButton("Cancel").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton("Register").amend {
          onClick --> { _ =>
            val command = Register(context.email.now())
            val response = CommandProxy.post(context.registerUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        }
      )
    )
}