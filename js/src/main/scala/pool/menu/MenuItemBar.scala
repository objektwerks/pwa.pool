package pool.menu

import com.raquo.laminar.api.L._

object MenuItemBar {
  def apply(anchors: Anchor*): Div = div(cls("w3-bar w3-white w3-text-indigo"), anchors)
}