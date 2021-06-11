package pool

import com.raquo.laminar.api.L._

object Entities {
  def render(context: Context, model: Model): Div = {
    println(context)
    println(model)
    div( idAttr("entities"), cls("w3-container"),
      div( cls("w3-panel w3-indigo"),
        h3("Pools"),
        ul( idAttr("pools"), cls("w3-ul") ),
        button( cls("w3-btn w3-text-indigo"), "Add" )
      )
    ) 
  }
}