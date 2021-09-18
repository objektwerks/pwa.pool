package pool.dialog

import com.raquo.laminar.api.L._

object Modal {
  def apply(id: String, divs: Div*): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          divs
        )
      )
    )
}