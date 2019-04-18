package tripletail

sealed trait Command extends Product with Serializable

final case class SignUp(email: String) extends Command

final case class SignIn(license: String, email: String) extends Command