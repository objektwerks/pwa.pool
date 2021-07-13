package pool.component

import com.raquo.laminar.api.L._

object Input {
  def apply(column: String,
            typeOf: String,
            isReadOnly: Boolean = false): Div =
    div(
      cls("w3-col"),
      width(column),
      input(
        cls("w3-input w3-hover-light-gray w3-text-indigo"),
        typ(typeOf),
        required(true),
        readOnly(isReadOnly)
      )
    )
}