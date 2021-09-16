package pool.view

import com.raquo.laminar.api.L._

object ListView {
  def apply(items: EventStream[List[Div]]): Div =
    div(
      cls("w3-margin"),
      children <-- items
    )
}