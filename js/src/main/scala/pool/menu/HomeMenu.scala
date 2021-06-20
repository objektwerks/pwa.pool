package pool.menu

import com.raquo.laminar.api.L._

import pool.dialog.{DeactivateDialog, LoginDialog, ReactivateDialog, RegisterDialog}

object HomeMenu {
  val id = getClass.getSimpleName
  val registerId = id + RegisterDialog.id
  val loginId = id + LoginDialog.id
  val deactivateId = id + DeactivateDialog.id
  val reactivateId = id + ReactivateDialog.id

  def apply(registerDialog: Div,
            loginDialog: Div,
            deactivateDialog: Div,
            reactivateDialog: Div): Div =
    div(idAttr(id), cls("w3-bar w3-white w3-text-indigo"),
      a(idAttr(registerId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> (_ => registerDialog.amend(display("block"))),
        "Register"),
      a(idAttr(loginId), href("#"), cls("w3-bar-item w3-button"),
        onClick --> (_ => loginDialog.amend(display("block"))),
        "Login"),
      a(idAttr(deactivateId), href("#"), cls("w3-bar-item w3-button"), display("none"),
        onClick --> (_ => deactivateDialog.amend(display("block"))),
        "Deactivate"),
      a(idAttr(reactivateId), href("#"), cls("w3-bar-item w3-button"), display("none"),
        onClick --> (_ => reactivateDialog.amend(display("block"))),
        "Reactivate"),
      registerDialog,
      loginDialog,
      deactivateDialog,
      reactivateDialog
    )
}