package pool.view

import com.raquo.laminar.api.L._

import pool._
import pool.component._
import pool.dialog.AccountDialog
import pool.handler.StateHandler
import pool.proxy.EntityProxy

object PoolsView {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  val pools = Var(Pools(Seq.empty[Pool]))

  def handler(context: Context, errors: EventBus[String], state: State): Unit = {
    state match {
      case pools: Pools => this.pools.set(pools)
      case id: Id => context.log(s"Todo Id: $id for add pool.")
      case count: Count => context.log(s"Todo Count: $count for update pool.")
      case _ => errors.emit(s"Invalid: $state")
    }
  }

  def pools(context: Context): Unit = {
    val license = License(AccountDialog.account.now().license)
    val response = EntityProxy.post(context.poolsUrl, license.key, license)
    StateHandler.handle(context, errors, response, handler)
  }

  def apply(context: Context): Div = {
    Container(id = id, isDisplayed = "none",
      Header("Pools"),
      Errors(errors),
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
}