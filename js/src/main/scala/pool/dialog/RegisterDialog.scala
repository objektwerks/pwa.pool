package pool.dialog

import com.raquo.laminar.api.L._

import pool.handler.EventHandler
import pool.menu.HomeMenu
import pool.proxy.CommandProxy
import pool._

object RegisterDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case registered: Registered =>
        context.account.set(registered.account)
        context.hide(HomeMenu.registerMenuItemId)
        context.hide(id)
      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          div(cls("w3-container w3-indigo"),
            h6("Register")
          ),
          div(cls("w3-panel w3-red"),
            child.text <-- errors.events
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("15%"),
              label(cls("w3-left-align w3-text-indigo"), "Email:")
            ),
            div(cls("w3-col"), width("85%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.email
              )
            )
          ),
          div(cls("w3-bar"),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Register(context.email.now())
                val response = CommandProxy.post(context.registerUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Register"
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