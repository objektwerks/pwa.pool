package pool

sealed trait Event extends Product with Serializable

final case class Registered(licensee: Licensee) extends Event

final case class LoggedIn(licensee: Licensee) extends Event

final case class Deactivated(licensee: Licensee) extends Event

final case class Reactivated(licensee: Licensee) extends Event