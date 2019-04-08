package tripletail

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Sequence(seq: Seq[Entity]) extends State

case object Ok extends State