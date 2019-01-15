package tripletail

sealed trait State extends Product with Serializable

final case class Id(id: Int) extends State

final case class Pools(pools: Seq[Pool]) extends State

final case class Surfaces(surfaces: Seq[Surface]) extends State
