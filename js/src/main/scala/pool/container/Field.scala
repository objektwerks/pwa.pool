package pool.container

import com.raquo.laminar.api.L._

object Field {
  def apply(label: Label, input: Input): Div =
    div(cls("w3-row w3-margin"),
      label,
      input
    )
}