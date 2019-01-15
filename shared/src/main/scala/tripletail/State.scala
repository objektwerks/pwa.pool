package tripletail

sealed trait State

final case class Id(id: Int) extends State