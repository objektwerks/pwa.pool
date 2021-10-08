package pool.text

import com.raquo.laminar.api.L._

object TextArea {
  def apply(column: String, textArea: TextArea): Div =
    div(
      cls("w3-col"),
      width(column),
      textArea
    )

  def field(rowCount: Int = 2, isReadOnly: Boolean = false): TextArea =
    textArea(
      cls("w3-hover-light-gray w3-text-indigo"),
      rows(rowCount),
      readOnly(isReadOnly)
    )
}