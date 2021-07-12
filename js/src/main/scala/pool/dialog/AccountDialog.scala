package pool.dialog

import com.raquo.laminar.api.L._

import pool._
import pool.component.{Errors, Header}
import pool.handler.EventHandler
import pool.menu.HomeMenu
import pool.proxy.CommandProxy

object AccountDialog {
  val id = getClass.getSimpleName
  val deactivateButtonId = id + "-deactivate-button"
  val reactivateButtonId = id + "-reactivate-button"
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit = {
    event match {
      case deactivated: Deactivated =>
        context.account.set(deactivated.account)
        context.hide(id)
        context.hide(HomeMenu.poolsMenuItemId)
      case reactivated: Reactivated =>
        context.account.set(reactivated.account)
        context.hide(id)
        context.show(HomeMenu.poolsMenuItemId)

      case _ => errors.emit(s"Invalid: $event")
    }
  }

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          Header("Account"),
          Errors(errors),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "License:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.account.signal.map(_.license))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Email:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.account.signal.map(_.email))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Pin:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.account.signal.map(_.pin.toString))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Activated:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.account.signal.map(_.activated.toString))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Deactivated:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.account.signal.map(_.deactivated.toString))
            )
          ),
          div(cls("w3-bar"),
            button(idAttr(deactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Deactivate(context.account.now().license)
                val response = CommandProxy.post(context.deactivateUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Deactivate"
            ),
            button(idAttr(reactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Reactivate(context.account.now().license)
                val response = CommandProxy.post(context.reactivateUrl, Account.emptyLicense, command)
                EventHandler.handle(context, errors, response, handler)
              },
              "Reactivate"
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