package pool.component

import com.raquo.laminar.api.L._

object Input {
  def apply(inputWidth: String,
            inputType: String,
            isReadOnly: Boolean): Div =
    div(
      cls("w3-col"),
      width(inputWidth),
      input(
        cls("w3-input w3-hover-light-gray w3-text-indigo"),
        typ(inputType),
        required(isReadOnly),
      )
    )
}