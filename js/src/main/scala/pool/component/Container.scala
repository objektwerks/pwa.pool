package pool.component

import com.raquo.laminar.api.L._

object Container {
  def apply(homeMenu: Div,
            poolsView: Div): Div =
    div(cls("w3-container"),
      homeMenu,
      poolsView
    )
}