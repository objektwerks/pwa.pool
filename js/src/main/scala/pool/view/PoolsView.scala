package pool.view

import com.raquo.laminar.api.L._

import pool._
import pool.container._
import pool.dialog.AccountDialog
import pool.handler.StateHandler
import pool.menu.{MenuButton, MenuButtonBar}
import pool.proxy.EntityProxy
import pool.text.{Errors, Header}

object PoolsView {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  var poolSignal: Signal[Seq[Pool]] = Signal.fromValue(Seq.empty[Pool])
  var listViewSignal: Signal[Seq[Div ]] = Signal.fromValue(Seq.empty[Div])

  def init(context: Context): Unit = {
    val license = License(AccountDialog.account.now().license)
    val response = EntityProxy.post(context.poolsUrl, license.key, license)
    StateHandler.handle(context, errors, response, handler)
  }

  def renderer(pool: Signal[Pool]): Div =
    div(
      child.text <-- pool.map(_.name)
    )

  def handler(context: Context, errors: EventBus[String], state: State): Unit =
    state match {
      case pools: Pools =>
        poolSignal = Signal.fromValue(pools.pools)
        listViewSignal = poolSignal.split(_.id)((_, _, pool) => renderer(pool) )
      case id: Id => context.log(s"Pool id: $id for add pool.")
      case count: Count => context.log(s"Pool count: $count for update pool.")
      case _ => errors.emit(s"Invalid: $state")
    }

  def apply(context: Context): Div =
    Container(id = id, isDisplayed = "none",
      Header("Pools"),
      Errors(errors),
      ListView(listViewSignal),
      MenuButtonBar(
        MenuButton(name = "Add").amend {
          onClick --> { _ =>
            context.log(context)
          }
        },
        MenuButton(name = "Edit").amend {
          onClick --> { _ =>
            context.log(context)
          }
        }
      )
    )
}