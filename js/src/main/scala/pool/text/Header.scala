package pool.text

import com.raquo.laminar.api.L._

object Header {
  def apply(name: String): Div =
    div(
      cls("w3-indigo"),
      h6(cls("w3-margin"), name)
    )
}