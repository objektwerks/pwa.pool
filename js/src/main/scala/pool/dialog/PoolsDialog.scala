package pool.dialog

import com.raquo.laminar.api.L._

import pool.container.Field
import pool.dialog.AccountDialog.account
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
      lat = 0,
      lon = 0,
      volume = 1000
    )
  )

  def apply(context: Context, pool: Var[Pool]): Div =
    Modal(id = id,
      Header("Pool"),
      Errors(errors),
      Field(
        Label(column = "25%", name = "License:"),
        Text(column = "75%", Text.field(typeOf = "text", isReadOnly = true).amend {
          value <-- account.signal.map(_.license)
        })
      ),
      Field(
        Label(column = "15%", name = "Name:"),
        Text(column = "85%", Text.field(typeOf = "text").amend {
          onInput.mapToValue.filter(_.nonEmpty) --> { name => pool.update( pool => pool.copy(name = name) ) }
        })
      )
    )
}