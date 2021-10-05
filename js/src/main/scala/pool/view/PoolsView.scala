package pool.view

import com.raquo.laminar.api.L._

import pool._
import pool.container._
import pool.dialog.{New, View, PoolDialog}
import pool.handler.StateHandler
import pool.menu.{MenuButton, MenuButtonBar}
import pool.proxy.EntityProxy
import pool.text.{Errors, Header}

object PoolsView {
  val id = getClass.getSimpleName
  val viewButtonId = id + "-view-button"
  val errors = new EventBus[String]
  var listItems = Var(Seq.empty[Li]).toObservable

  def handler(context: Context, errors: EventBus[String], state: State): Unit =
    state match {
      case pools: Pools =>
        context.pools.set(pools.pools)
        listItems = context.pools.signal.split(_.id)((_, _, pool) =>
          ListView.renderItem( pool.map(_.name) ).amend {
            onClick.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { id =>
              context.pools.now().find(_.id == id).foreach(pool => context.pool.set(pool))
              context.enable(viewButtonId)
            }
          }
        )
      case id: Id =>
        val pool = context.pool.now().copy(id = id.id)
        context.pool.set( pool )
        context.pools.update(_ :+ pool)
      case count: Count => if (count.count != 1) errors.emit(s"Update failed: $count")
      case _ => errors.emit(s"Invalid state: $state")
    }

  def load(context: Context): Unit = {
    val license = License(context.account.now().license)
    val response = EntityProxy.post(context.poolsUrl, license.key, license)
    StateHandler.handle(context, errors, response, handler)
  }

  def apply(context: Context, poolDialog: Div): Div =
    Container(id = id, isDisplayed = "none",
      Header("Pools"),
      Errors(errors),
      ListView(listItems),
      MenuButtonBar(
        MenuButton(name = "New").amend {
          onClick --> { _ =>
            context.pool.set( Pool.emptyPool.copy(license = context.account.now().license) )
            PoolDialog.applyMode(New, context)
            poolDialog.amend(display("block"))
          }
        },
        MenuButton(id = viewButtonId, name = "View", isDisabled = true).amend {
          onClick --> { _ =>
            PoolDialog.applyMode(View, context)
            poolDialog.amend(display("block"))
          }
        },
        MenuButton(name = "Refresh").amend {
          onClick --> { _ =>
            load(context)
          }
        }
      )
    )
}