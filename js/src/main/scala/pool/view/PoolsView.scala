package pool.view

import com.raquo.laminar.api.L._

import pool._

object PoolsView {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def handler(context: Context, errors: EventBus[String], state: State): Unit = {
    state match {
      case pools: Pools => context.pools.set(pools)
      case _ => errors.emit(s"Invalid: $state")
    }
  }

  def pools(context: Context): Unit = {
    val license = License(context.account.now().license)
    val response = ServerProxy.post(context.poolsUrl, license.key, license)
    Handler.onState(context, errors, response, handler)
  }

  def apply(context: Context): Div = {
    println(context)
    div(idAttr(id), cls("w3-container"), display("none"),
      h6(cls("w3-indigo"), "Pools"),
      div(cls("w3-panel w3-red"),
        child.text <-- errors.events
      ),
      ul(idAttr("pools"), cls("w3-ul w3-hoverable")),
      div(cls("w3-bar"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Add"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Edit")
      )
    )
  }
}