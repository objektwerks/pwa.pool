package tripletail

sealed abstract class Event

final case class SignedIn(licensee: Licensee) extends Event