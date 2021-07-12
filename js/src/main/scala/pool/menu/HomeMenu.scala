package pool.menu

import com.raquo.laminar.api.L._

import pool.Context
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
    div(idAttr(id), cls("w3-bar w3-margin w3-white w3-text-indigo"),
      a(idAttr(registerMenuItemId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> { _ =>
          registerDialog.amend(display("block"))
        },
        "Register"
      ),
      a(idAttr(loginMenuItemId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> { _ =>
          loginDialog.amend(display("block"))
        },
        "Login"
      ),
      a(idAttr(accountMenuItemId), href("#"), cls("w3-bar-item w3-button"), display("none"),
        onClick --> { _ =>
          accountDialog.amend(display("block"))
          if (context.account.now().deactivated == 0) {
            context.show(AccountDialog.deactivateButtonId)
            context.hide(AccountDialog.reactivateButtonId)
          } else {
            context.hide(AccountDialog.deactivateButtonId)
            context.show(AccountDialog.reactivateButtonId)
          }
        },
        "Account"
      ),
      a(idAttr(poolsMenuItemId), href("#"), cls("w3-bar-item w3-button"), display("none"),
        onClick --> { _ =>
          PoolsView.pools(context)
          context.show(PoolsView.id)
          context.hide(poolsMenuItemId)
        },
        "Pools"
      ),
      registerDialog,
      loginDialog,
      accountDialog
    )
}