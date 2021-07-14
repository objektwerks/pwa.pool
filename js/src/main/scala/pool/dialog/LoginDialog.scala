package pool.dialog

import com.raquo.laminar.api.L._

import pool.handler.EventHandler
import pool.menu.HomeMenu
import pool.proxy.CommandProxy
import pool._
import pool.component.{Errors, Field, Header, Label, Text}

object LoginDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case loggedin: LoggedIn =>
        context.account.set(loggedin.account)
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(HomeMenu.loginMenuItemId)
        context.show(HomeMenu.accountMenuItemId)
        context.show(HomeMenu.poolsMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          Header("Login"),
          Errors(errors),
          Field(
            Label(column = "15%", name = "Pin:"),
            Text(column = "85%", Text.field(typeOf = "number").amend {
              onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.pin
            })
          ),
          div(cls("w3-bar"),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Login(context.pin.now())
                val response = CommandProxy.post(context.loginUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Login"
            ),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> (_ => context.hide(id)),
              "Cancel"
            )
          )
        )
      )
    )
}