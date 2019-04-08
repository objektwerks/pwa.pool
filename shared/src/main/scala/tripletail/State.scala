package tripletail

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Count(value: Int) extends State

final case class Sequence(entities: Seq[Entity]) extends State