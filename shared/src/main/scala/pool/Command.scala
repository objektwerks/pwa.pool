package pool

sealed trait Command extends Product with Serializable

final case class Register(email: String) extends Command

final case class Login(email: String, pin: String) extends Command

final case class Deactivate(license: String) extends Command

final case class Reactivate(license: String) extends Command