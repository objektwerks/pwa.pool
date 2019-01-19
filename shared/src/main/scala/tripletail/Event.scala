package tripletail

sealed trait Event extends Product with Serializable

final case class Signedup(licensee: Licensee) extends Event

final case class Signedin(licensee: Licensee, pools: Seq[Pool]) extends Event