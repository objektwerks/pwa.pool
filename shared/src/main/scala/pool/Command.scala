package pool

sealed trait Command extends Product with Serializable

final case object CheckHealth extends Command

final case class SignUp(emailAddress: String) extends Command

final case class ActivateLicensee(license: String, emailAddress: String) extends Command

final case class SignIn(license: String, emailAddress: String) extends Command

final case class DeactivateLicensee(license: String, emailAddress: String) extends Command