package pool

import com.raquo.laminar.api.L._

object Entities {
  def render(context: Context, model: Model): Div = 
    div( idAttr("entities"), cls("w3-container"),
      label( context.toString() ),
      label( model.toString() )
    ) 
}