package pool.menu

import com.raquo.laminar.api.L._

object MenuItem {
  def apply(id: String,
            name: String,
            isDisplayed: String = "block"): Anchor =
    a(idAttr(id), href("#"), cls("w3-bar-item w3-button"), display(isDisplayed), name)
}