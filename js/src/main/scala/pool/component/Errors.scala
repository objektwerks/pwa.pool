package pool.component

import com.raquo.laminar.api.L._

object Errors {
  def apply(errors: EventBus[String]): Div =
    div(
      cls("w3-panel w3-red"),
      child.text <-- errors.events
    )
}