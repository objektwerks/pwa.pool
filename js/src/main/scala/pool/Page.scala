package pool

import com.raquo.laminar.api.L.*

sealed trait Page:
  val title = "Poolmate"

case object RootPage extends Page
case object RegisterPage extends Page
case object LoginPage extends Page

case object AppPage extends Page
case object AccountPage extends Page
case object PoolsPage extends Page

sealed trait EntityPage extends Page:
  val id: Long

final case class PoolPage(id: Long = 0) extends EntityPage