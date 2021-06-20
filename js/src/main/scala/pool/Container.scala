package pool

import com.raquo.laminar.api.L._

object Container {
  val id = getClass.getSimpleName

  def apply(homeMenu: Div,
            poolsView: Div): Div =
    div(idAttr(id), cls("w3-container"),
      homeMenu,
      poolsView
    )
}