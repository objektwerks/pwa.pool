package pool

import com.raquo.laminar.api.L._

object Entities {
  def render(context: Context, model: Model): Div = {
    println(context)
    println(model)
    div( idAttr("entities"), cls("w3-container"),
    
    ) 
  }
}