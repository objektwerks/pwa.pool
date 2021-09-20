package pool.dialog

import com.raquo.laminar.api.L._

import pool.container.Field
import pool.menu.{MenuButton, MenuButtonBar}
import pool.text.{Errors, Header, Label, Text}
import pool.{Context, Pool}

object PoolsDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]
  val emptyPool = Var(
    Pool(
      license = "",
      name = "pool",
      built = 0,
      lat = 0.0,
      lon = 0.0,
      volume = 1000
    )
  )

  def apply(context: Context): Div = apply(context, emptyPool)

  def apply(context: Context, pool: Var[Pool]): Div =
    Modal(id = id,
      Header("Pool"),
      Errors(errors),
      Field(
        Label(column = "25%", name = "License:"),
        Text(column = "75%", Text.field(typeOf = "text", isReadOnly = true).amend {
          value <-- AccountDialog.account.signal.map(_.license)
        })
      ),
      Field(
        Label(column = "25%", name = "Name:"),
        Text(column = "75%", Text.field(typeOf = "text").amend {
          onInput.mapToValue.filter(_.nonEmpty) --> { name =>
            pool.update( pool => pool.copy(name = name) )
          }
        })
      ),
      Field(
        Label(column = "25%", name = "Built:"),
        Text(column = "75%", Text.field(typeOf = "number").amend {
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { built =>
            pool.update( pool => pool.copy(built = built) )
          }
        })
      ),
      Field(
        Label(column = "25%", name = "Lat:"),
        Text(column = "75%", Text.field(typeOf = "number").amend {
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { lat =>
            pool.update( pool => pool.copy(lat = lat) )
          }
        })
      ),
      Field(
        Label(column = "25%", name = "Lon:"),
        Text(column = "75%", Text.field(typeOf = "number").amend {
          onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> { lon =>
            pool.update( pool => pool.copy(lon = lon) )
          }
        })
      ),
      Field(
        Label(column = "25%", name = "Volume:"),
        Text(column = "75%", Text.field(typeOf = "number").amend {
          onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> { volume =>
            pool.update( pool => pool.copy(volume = volume) )
          }
        })
      ),
      MenuButtonBar(
        MenuButton(name = "Cancel").amend {
          onClick --> { _ => context.hide(id) }
        }
      )
    )
}