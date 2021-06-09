package pool

import com.raquo.laminar.api.L._

object Content {
  def render(navigation: Div): Div = 
    div( 
      navigation, 
      div( idAttr("pools"), cls("w3-container")

      ) 
    )
}