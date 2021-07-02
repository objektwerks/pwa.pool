package pool

sealed trait Command extends Product with Serializable

final case class Register(email: String) extends Command

final case class Login(pin: Int) extends Command

final case class DeactivateLicensee(license: String) extends Command

final case class ReactivateLicensee(license: String) extends Command