package pool.dialog

import com.raquo.laminar.api.L._

import pool.Context

object DeactivateDialog {
  val id = getClass.getSimpleName

  def apply(context: Context): Div = {
    println(context)
    div( idAttr(id) )
  }
}