package tripletail

sealed trait Command extends Product with Serializable

final case class SignUp(email: String) extends Command

final case class SignIn(license: String, email: String) extends Command

final case class ListPools(license: String) extends Command

final case class SavePool(license: String, pool: Pool) extends Command

final case class ListSurfaces(license: String, poolId: Int) extends Command

final case class SaveSurface(license: String, surface: Surface) extends Command

final case class ListPumps(license: String, poolId: Int) extends Command

final case class SavePump(license: String, pump: Pump) extends Command