package pool.component

import com.raquo.laminar.api.L._

object Text {
  def apply(column: String,
            input: Input): Div =
    div(
      cls("w3-col"),
      width(column),
      input
    )

  def field(typeOf: String,
            isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true),
      readOnly(isReadOnly)
    )
}