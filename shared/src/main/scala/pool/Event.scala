package pool

sealed trait Event extends Product with Serializable

final case class Registered(licensee: Licensee) extends Event

final case class SignedIn(licensee: Licensee) extends Event

final case class LicenseeDeactivated(licensee: Licensee) extends Event

final case class LicenseeReactivated(licensee: Licensee) extends Event