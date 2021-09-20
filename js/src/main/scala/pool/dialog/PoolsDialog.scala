package pool.dialog

import com.raquo.laminar.api.L._

import java.time.LocalDate

import pool.text.{Errors, Header}
import pool.{Context, Pool}

object PoolsDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  val emptyPool = Pool(
    license = AccountDialog.account.now().license,
    name = "pool",
    built = LocalDate.now().getYear,
    lat = 0,
    lon = 0,
    volume = 1000)

  def apply(context: Context): Div =
    Modal(id = id,
      Header("Pool"),
      Errors(errors)
    )
}