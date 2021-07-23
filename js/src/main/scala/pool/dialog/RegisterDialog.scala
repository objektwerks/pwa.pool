package pool.dialog

import com.raquo.laminar.api.L._

import pool.handler.EventHandler
import pool.menu.HomeMenu
import pool.proxy.CommandProxy
import pool._
import pool.component._

object RegisterDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  val email = Var("")

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case Registering() =>
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Register"),
      Note("Provided your email address is valid, you will receive your new account information via email."),
      Errors(errors),
      Field(
        Label(column = "15%", name = "Email:"),
        Text(column = "85%", Text.field(typeOf = "email").amend {
          onInput.mapToValue.filter(_.nonEmpty) --> email
        })
      ),
      MenuButtonBar(
        MenuButton(name = "Register").amend {
          onClick --> { _ =>
            val command = Register(email.now())
            val response = CommandProxy.post(context.registerUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        },
        MenuButton(name = "Cancel").amend {
          onClick --> { _ => context.hide(id) }
        }
      )
    )
}