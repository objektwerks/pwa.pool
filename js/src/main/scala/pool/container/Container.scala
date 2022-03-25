package pool.container

import com.raquo.laminar.api.L._

object Container {
  def apply(divs: Div*): Div = div(cls("w3-container"), divs )

  def apply(id: String, divs: Div*): Div = div(idAttr(id), cls("w3-container"), divs )

  def apply(id: String,
            isDisplayed: String,
            divs: Div*): Div =
    div(idAttr(id), cls("w3-container"), display(isDisplayed), divs )
}