package pool.component

import com.raquo.laminar.api.L._

object Text {
  val textCss = "w3-input w3-hover-light-gray w3-text-indigo"

  def apply(column: String,
            typeOf: String): Div =
    div(
      cls("w3-col"),
      width(column),
      input(
        cls(textCss),
        typ(typeOf),
        required(true)
      )
    )

  def readonly(typeOf: String): Input =
    input(
      cls(textCss),
      typ(typeOf),
      readOnly(true)
    )

  def wrapper(column: String,
              input: Input): Div =
    div(
      cls("w3-col"),
      width(column),
      input
    )
}