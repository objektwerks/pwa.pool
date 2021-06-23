package pool

sealed trait Command extends Product with Serializable

final case class SignUp(email: String) extends Command

final case class SignIn(email: String, pin: Int) extends Command

final case class DeactivateLicensee(license: String, email: String, pin: Int) extends Command

final case class ReactivateLicensee(license: String, email: String, pin: Int) extends Command