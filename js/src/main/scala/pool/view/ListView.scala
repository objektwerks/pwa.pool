package pool.view

import com.raquo.laminar.api.L._

object ListView {
  def apply(items: Signal[Seq[Li]]): Div =
    div(
      cls("w3-container"),
      ul(
        cls("w3-ul w3-hoverable"),
        children <-- items
      )
    )

  def renderItem(item: Signal[String]): Li =
    li(
      child.text <-- item
    )
}