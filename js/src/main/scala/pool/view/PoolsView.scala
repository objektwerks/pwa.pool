package pool.view

import com.raquo.laminar.api.L._

import pool.Context

object PoolsView {
  def render(context: Context): Div = {
    println(context)
    div( idAttr("poolsView"), cls("w3-container"),
      ul( idAttr("pools"), cls("w3-ul w3-hoverable"),
        li( h3("Pools") )
      ),
      div( cls("w3-row w3-padding-16"),
        button( cls("w3-btn w3-text-indigo"), "Add" ),
        button( cls("w3-btn w3-text-indigo"), "Update" )
      )
    )
  }
}