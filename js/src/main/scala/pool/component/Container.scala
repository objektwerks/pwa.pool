package pool.component

import com.raquo.laminar.api.L._

object Container {
  def apply(divs: Div*): Div = {
    div(cls("w3-container")).amendThis( _ => divs )
  }
}