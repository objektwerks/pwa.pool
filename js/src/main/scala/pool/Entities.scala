package pool

import com.raquo.laminar.api.L._

object Entities {
  def render(context: Context): Div = {
    println(context)
    div( cls("w3-container"),
      div( cls("w3-panel w3-indigo"),
        h3("Pools")
      ),
      ul( idAttr("pools"), cls("w3-ul w3-hoverable") ),
      button( cls("w3-btn w3-text-indigo"), "Add" ),
      button( cls("w3-btn w3-text-indigo"), "Update" )
    )
  }
}