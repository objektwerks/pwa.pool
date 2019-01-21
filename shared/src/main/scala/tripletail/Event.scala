package tripletail

sealed trait Event extends Product with Serializable

final case class Fault(message: String) extends Event

final case class SignedUp(licensee: Licensee) extends Event

final case class SignedIn(licensee: Licensee, pools: Seq[Pool]) extends Event