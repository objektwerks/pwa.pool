package tripletail

sealed abstract class Event

final case class SignedUp(licensee: Licensee) extends Event

final case class SignedIn(licensee: Licensee, pools: Seq[Pool]) extends Event