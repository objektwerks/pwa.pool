package pool.menu

import com.raquo.laminar.api.L._

object PoolsMenu {
  val id = getClass.getSimpleName

  def apply(deactivateDialog: Div, reactivateDialog: Div): Div =
    div(idAttr(id), cls("w3-bar w3-white w3-text-indigo"), display("none"),
      a(href("#"),
        cls("w3-bar-item w3-button"),
        onClick --> (_ => deactivateDialog.amend(display("block"))),
        "Deactivate"),
      a(href("#"),
        cls("w3-bar-item w3-button"),
        onClick --> (_ => reactivateDialog.amend(display("block"))),
        "Reactivate"),
      deactivateDialog,
      reactivateDialog
    )
}