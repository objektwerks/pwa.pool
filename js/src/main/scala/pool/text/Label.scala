package pool.text

import com.raquo.laminar.api.L._

object Label {
  def apply(column: String,
            name: String): Div =
    div(
      cls("w3-col"),
      width(column),
      label(cls("w3-left-align w3-text-indigo"), name)
    )
}