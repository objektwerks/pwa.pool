package pool.component

import com.raquo.laminar.api.L._

object Field {
  def apply(label: Div,
            input: Div): Div =
    div(cls("w3-row w3-margin"),
      label,
      input
    )
}