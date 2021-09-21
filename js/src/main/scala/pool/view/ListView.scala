package pool.view

import com.raquo.laminar.api.L._

object ListView {
  def apply(listItems: Signal[Seq[Li]]): Div =
    div(
      cls("w3-container w3-margin"),
      ul(
        cls("w3-ul w3-hoverable"),
        children <-- listItems
      )
    )
}