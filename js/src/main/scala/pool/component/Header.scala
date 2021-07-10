package pool.component

import com.raquo.laminar.api.L._

object Header {
  def apply(name: String): Div =
    div(
      cls("w3-container w3-indigo"),
      h6(name)
    )
}