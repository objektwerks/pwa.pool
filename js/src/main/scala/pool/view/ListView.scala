package pool.view

import com.raquo.laminar.api.L._

object ListView {
  def apply(entityStream: EventStream[Seq[Div]]): Div =
    div(
      cls("w3-margin"),
      children <-- entityStream
    )
}