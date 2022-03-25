package pool.text

import com.raquo.laminar.api.L._

object Messages {
  def apply(messages: EventBus[String]): Div =
    div(
      cls("w3-border-white w3-margin w3-yellow w3-text-indigo"),
      child.text <-- messages.events
    )
}