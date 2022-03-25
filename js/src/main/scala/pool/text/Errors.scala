package pool.text

import com.raquo.laminar.api.L._

object Errors {
  def apply(errors: EventBus[String]): Div =
    div(
      cls("w3-border-white w3-margin w3-red"),
      child.text <-- errors.events
    )
}