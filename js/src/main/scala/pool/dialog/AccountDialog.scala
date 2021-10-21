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
        context.hideAndShow(id, HomeMenu.poolsMenuItemId)
      case _ => errors.emit(s"Invalid: $event")
    }

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Account"),
      Errors(errors),
      Field(
        Label("License"),
        Text.readonly().amend {
          value <-- context.account.signal.map(_.license)
        }
      ),
      Field(
        Label("Email Address"),
        Text.readonly().amend {
          value <-- context.account.signal.map(_.email)
        }
      ),
      Field(
        Label("Pin"),
        Text.readonly().amend {
          value <-- context.account.signal.map(_.pin)
        }
      ),
      Field(
        Label("Activated"),
        Text.readonly().amend {
          value <-- context.account.signal.map(_.activated.toString)
        }
      ),
      Field(
        Label("Deactivated"),
        Text.readonly().amend {
          value <-- context.account.signal.map(_.deactivated.toString)
        }
      ),
      MenuButtonBar(
        MenuButton("Close").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton(deactivateButtonId, "Deactivate").amend {
          onClick --> { _ =>
            val command = Deactivate(context.account.now().license)
            val response = CommandProxy.post(context.deactivateUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        },
        MenuButton(reactivateButtonId, "Reactivate").amend {
          onClick --> { _ =>
            val command = Reactivate(context.account.now().license)
            val response = CommandProxy.post(context.reactivateUrl, Account.emptyLicense, command)
            EventHandler.handle(context, errors, response, handler)
          }
        }
      )
    )
}