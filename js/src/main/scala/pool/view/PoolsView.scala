package pool.view

import com.raquo.laminar.api.L._

import pool.Context

object PoolsView {
  val id = getClass.getSimpleName

  def apply(context: Context): Div = {
    println(context)
    div(idAttr(id), cls("w3-container w3-margin"), display("none"),
      p(cls("w3-indigo"), "Pools"),
      ul(idAttr("pools"), cls("w3-ul w3-hoverable")),
      div(cls("w3-row w3-padding-16"),
        button(cls("w3-btn w3-text-indigo"), "Add"),
        button(cls("w3-btn w3-text-indigo"), "Update")
      )
    )
  }
}