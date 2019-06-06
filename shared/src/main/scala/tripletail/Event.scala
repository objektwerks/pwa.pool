package tripletail

sealed trait Event extends Product with Serializable

final case class SignedUp(licensee: Licensee) extends Event

final case class SignedIn(licensee: Licensee) extends Event