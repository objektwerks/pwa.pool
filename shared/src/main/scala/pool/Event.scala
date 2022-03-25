package pool

sealed trait Event extends Product with Serializable

final case class Registering(inProgress: Boolean = true) extends Event

final case class LoggedIn(account: Account) extends Event

final case class Deactivated(account: Account) extends Event

final case class Reactivated(account: Account) extends Event