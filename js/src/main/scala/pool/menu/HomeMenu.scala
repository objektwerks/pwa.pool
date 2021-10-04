package pool.menu

import com.raquo.laminar.api.L._

import pool.Context
import pool.Validators._
import pool.container._
import pool.dialog.AccountDialog
import pool.view.PoolsView

object HomeMenu {
  val id = getClass.getSimpleName
  val registerMenuItemId = id + "-register-menu-item"
  val loginMenuItemId = id + "-login-menu-item"
  val accountMenuItemId = id + "-account-menu-item"
  val poolsMenuItemId = id + "-pools-menu-item"

  def apply(context: Context,
            registerDialog: Div,
            loginDialog: Div,
            accountDialog: Div): Div =
    Container(
      MenuItemBar(
        MenuItem(id = registerMenuItemId, name = "Register").amend {
          onClick --> { _ =>
            registerDialog.amend(display("block"))
          }
        },
        MenuItem(id = loginMenuItemId, name = "Login").amend {
          onClick --> { _ =>
            loginDialog.amend(display("block"))
          }
        },
        MenuItem(id = accountMenuItemId, name = "Account", isDisplayed = "none").amend {
          display("none")
          onClick --> { _ =>
            accountDialog.amend(display("block"))
            if (context.account.now().isActivated) {
              context.show(AccountDialog.deactivateButtonId)
              context.hide(AccountDialog.reactivateButtonId)
            } else if (context.account.now().isDeactivated) {
              context.hide(AccountDialog.deactivateButtonId)
              context.show(AccountDialog.reactivateButtonId)
            }
          }
        },
        MenuItem(id = poolsMenuItemId, name = "Pools", isDisplayed = "none").amend {
          display("none")
          onClick --> { _ =>
            PoolsView.load(context)
            context.show(PoolsView.id)
            context.hide(poolsMenuItemId)
          }
        }
      ),
      registerDialog,
      loginDialog,
      accountDialog
    )
}