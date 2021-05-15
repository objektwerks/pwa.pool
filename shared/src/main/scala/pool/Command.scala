package pool

sealed trait Command extends Product with Serializable

final case class SignUp(emailAddress: String) extends Command

final case class SignIn(emailAddress: String, pin: Int) extends Command

final case class DeactivateLicensee(license: String, emailAddress: String, pin: Int) extends Command

final case class ReactivateLicensee(license: String, emailAddress: String, pin: Int) extends Command