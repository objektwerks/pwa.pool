package pool.view

import com.raquo.laminar.api.L._

import pool.Context
import pool.container.{Container, Field}
import pool.dialog.{Mode, New, View}
import pool.handler.StateHandler
import pool.menu.{MenuButton, MenuButtonBar}
import pool.proxy.EntityProxy
import pool.text.{Errors, Header, Label, Text}

object PoolView {
  val id = getClass.getSimpleName
  val addButtonId = id + "-add-button"
  val updateButtonId = id + "-update-button"
  val errors = new EventBus[String]

  def handler(context: Context, url: String): Unit = {
    val pool = context.pool.now()
    val response = EntityProxy.post(url, context.account.now().license, pool)
    StateHandler.handle(context, errors, response, PoolsView.handler)
  }

  def applyMode(mode: Mode, context: Context): Unit = mode match {
    case New =>
      context.disable(updateButtonId)
      context.enable(addButtonId)
    case View =>
      context.disable(addButtonId)
      context.enable(updateButtonId)
  }

  def apply(context: Context): Div =
    Container(id = id, isDisplayed = "none",
      Header("Pool"),
      Errors(errors),
      Field(
        Label("License"),
        Text.readonly().amend {
          value <-- context.pool.signal.map(_.license)
        }
      ),
      Field(
        Label("Name"),
        Text.text().amend {
          value <-- context.pool.signal.map(_.name)
          onInput.mapToValue.filter(_.nonEmpty) --> { name =>
            context.pool.update(pool => pool.copy(name = name))
          }
        }
      ),
      Field(
        Label("Year Built"),
        Text.integer().amend {
          minLength(4)
          maxLength(4)
          value <-- context.pool.signal.map(_.built.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { built =>
            context.pool.update(pool => pool.copy(built = built))
          }
        }
      ),
      Field(
        Label("Volume"),
        Text.integer().amend {
          minLength(4)
          maxLength(5)
          value <-- context.pool.signal.map(_.volume.toString)
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { volume =>
            context.pool.update(pool => pool.copy(volume = volume))
          }
        }
      ),
      MenuButtonBar(
        MenuButton("Close").amend {
          onClick --> { _ => context.hide(id) }
        },
        MenuButton(addButtonId, "Add").amend {
          onClick --> { _ =>
            handler(context, context.poolsAddUrl)
            context.hideAndShow(id, PoolsView.id)
          }
        },
        MenuButton(updateButtonId, "Update").amend {
          onClick --> { _ =>
            handler(context, context.poolsUpdateUrl)
            context.hideAndShow(id, PoolsView.id)
          }
        }
      )
    )
}