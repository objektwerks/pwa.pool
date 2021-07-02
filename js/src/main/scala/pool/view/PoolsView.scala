package pool.view

import com.raquo.laminar.api.L._

import pool.Context

object PoolsView {
  val id = getClass.getSimpleName

  def apply(context: Context): Div = {
    println(context)
    div(idAttr(id), cls("w3-container"), display("none"),
      h6(cls("w3-indigo"), "Pools"),
      ul(idAttr("pools"), cls("w3-ul w3-hoverable")),
      div(cls("w3-bar"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Add"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Edit")
      )
    )
  }
}