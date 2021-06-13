package pool

import com.raquo.laminar.api.L._

object Content {
  def apply(registerLoginMenu: Div, poolsView: Div): Div =
    div( idAttr("content"), cls("w3-container"),
      registerLoginMenu,
      poolsView
    )
}