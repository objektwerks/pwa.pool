package pool

sealed trait Command extends Product with Serializable

final case class SignUp(emailAddress: String) extends Command

final case class ActivateLicensee(emailAddress: String, license: String, pin: Int) extends Command

final case class SignIn(emailAddress: String, pin: Int) extends Command

final case class DeactivateLicensee(emailAddress: String, license: String, pin: Int) extends Command