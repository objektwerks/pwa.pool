package pool

import com.raquo.laminar.api.L._

object Content {
  def render(navigation: Div, entities: Div): Div = 
    div( 
      navigation, 
      entities
    )
}