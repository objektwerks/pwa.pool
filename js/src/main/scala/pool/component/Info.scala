package pool.component

import com.raquo.laminar.api.L._

object Info {
  def apply(message: String): Div =
    div(
      cls("w3-panel w3-indigo"),
      message
    )
}