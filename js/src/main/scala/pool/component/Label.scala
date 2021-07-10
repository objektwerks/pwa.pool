package pool.component

import com.raquo.laminar.api.L._

object Label {
  def apply(labelWidth: String,
            labelName: String): Div =
    div(
      cls("w3-col"),
      width(labelWidth),
      label(cls("w3-left-align w3-text-indigo"), labelName)
    )
}