package pool.menu

import com.raquo.laminar.api.L._

import pool.Context
import pool.dialog.{AccountDialog, LoginDialog, RegisterDialog}
import pool.view.PoolsView

object HomeMenu {
  val id = getClass.getSimpleName
  val registerMenuItemId = id + "-" + RegisterDialog.id
  val loginMenuItemId = id + "-" + LoginDialog.id
  val accountMenuItemId = id + "-" + AccountDialog.id
  val poolsMenuItemId = id + "-" + PoolsView.id

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
          // Todo: load pools
          context.show(PoolsView.id)
        },
        "Pools"
      ),
      registerDialog,
      loginDialog,
      accountDialog
    )
}