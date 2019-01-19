package tripletail

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Sequence[T](seq: Seq[T]) extends State