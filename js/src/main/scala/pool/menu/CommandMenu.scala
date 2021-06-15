package pool.menu

import com.raquo.laminar.api.L._

object CommandMenu {
  val id = getClass.getSimpleName

  def apply(registerDialog: Div,
            loginDialog: Div,
            deactivateDialog: Div,
            reactivateDialog: Div): Div =
    div( idAttr(id), cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => registerDialog.amend( display("block") ) ),
         "Register" ),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => loginDialog.amend( display("block") ) ),
         "Login" ),
      a( href("#"),
        cls("w3-bar-item w3-button"),
        onClick --> (_ => deactivateDialog.amend( display("block") ) ),
        "Deactivate" ),
      a( href("#"),
        cls("w3-bar-item w3-button"),
        onClick --> (_ => reactivateDialog.amend( display("block") ) ),
        "Reactivate" ),
      registerDialog,
      loginDialog,
      deactivateDialog,
      reactivateDialog
    )
}