package pool.component

import com.raquo.laminar.api.L._

object Messages {
  def apply(messages: EventBus[String]): Div =
    div(
      cls("w3-border-white w3-indigo"),
      child.text <-- messages.events
    )
}