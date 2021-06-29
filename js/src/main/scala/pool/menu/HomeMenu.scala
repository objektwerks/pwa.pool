package pool.menu

import com.raquo.laminar.api.L._

import pool.Context
import pool.dialog.AccountDialog.{deactivateId, reactivateId}
import pool.dialog.{AccountDialog, LoginDialog, RegisterDialog}

object HomeMenu {
  val id = getClass.getSimpleName
  val registerId = id + RegisterDialog.id
  val loginId = id + LoginDialog.id
  val accountId = id + AccountDialog.id

  def apply(context: Context,
            registerDialog: Div,
            loginDialog: Div,
            accountDialog: Div): Div =
    div(idAttr(id), cls("w3-bar w3-margin w3-white w3-text-indigo"),
      a(idAttr(registerId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> (_ => registerDialog.amend(display("block"))),
        "Register"),
      a(idAttr(loginId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> (_ => loginDialog.amend(display("block"))),
        "Login"),
      a(idAttr(accountId), href("#"), cls("w3-bar-item w3-button"), display("none"),
        onClick --> { _ =>
          accountDialog.amend(display("block"))
          if (context.licensee.now().deactivated == 0) {
            context.show(deactivateId)
            context.hide(reactivateId)
          } else {
            context.hide(deactivateId)
            context.show(reactivateId)
          }
        },
        "Account"),
      registerDialog,
      loginDialog,
      accountDialog
    )
}