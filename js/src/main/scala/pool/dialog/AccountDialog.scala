package pool.dialog

import com.raquo.laminar.api.L._

import pool._
import pool.container._
import pool.handler.EventHandler
import pool.menu.{HomeMenu, MenuButton, MenuButtonBar}
import pool.proxy.CommandProxy
import pool.text.{Errors, Header, Label, Text}
import pool.view.PoolsView

object AccountDialog {
  val id = getClass.getSimpleName
  val deactivateButtonId = id + "-deactivate-button"
  val reactivateButtonId = id + "-reactivate-button"
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], event: Event): Unit =
    event match {
      case deactivated: Deactivated =>
        context.account.set(deactivated.account)
        context.hide(HomeMenu.poolsMenuItemId)
        context.hide(PoolsView.id)
        context.hide(id)
      case reactivated: Reactivated =>
        context.account.set(reactivated.account)
        context.hide(id)
        context.show(HomeMenu.poolsMenuItemId)
      case _ => errors.emit(s"Invalid: $event")
    }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Account"),
      Errors(errors),
      Field(
        Label(column = "25%", name = "License:"),
        Text(column = "75%", Text.readonly(typeOf = "text").amend {
          value <-- context.account.signal.map(_.license)
        })
      ),
      Field(
        Label(column = "25%", name = "Email:"),
        Text(column = "75%", Text.readonly(typeOf = "text").amend {
          value <-- context.account.signal.map(_.email)
        })
      ),
      Field(
        Label(column = "25%", name = "Pin:"),
        Text(column = "75%", Text.readonly(typeOf = "text").amend {
          value <-- context.account.signal.map(_.pin.toString)
        })
      ),
      Field(
        Label(column = "25%", name = "Activated:"),
        Text(column = "75%", Text.readonly(typeOf = "text").amend {
          value <-- context.account.signal.map(_.activated.toString)
        })
      ),
      Field(
        Label(column = "25%", name = "Deactivated:"),
        Text(column = "75%", Text.readonly(typeOf = "text").amend {
          value <-- context.account.signal.map(_.deactivated.toString)
        })
      ),
      MenuButtonBar(
        MenuButton(name = "Cancel").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton(id = deactivateButtonId, name = "Deactivate").amend {
          onClick --> { _ =>
            val command = Deactivate(context.account.now().license)
            val response = CommandProxy.post(context.deactivateUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        },
        MenuButton(id = reactivateButtonId, name = "Reactivate").amend {
          onClick --> { _ =>
            val command = Reactivate(context.account.now().license)
            val response = CommandProxy.post(context.reactivateUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        }
      )
    )
}