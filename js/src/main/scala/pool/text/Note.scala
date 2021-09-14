package pool.text

import com.raquo.laminar.api.L._

object Note {
  def apply(note: String): Div =
    div(
      cls("w3-indigo"),
      p(cls("w3-margin"), note)
    )
}