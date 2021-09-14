package pool.menu

import com.raquo.laminar.api.L._

object MenuButtonBar {
  def apply(buttons: Button*): Div = div(cls("w3-bar"), buttons)
}