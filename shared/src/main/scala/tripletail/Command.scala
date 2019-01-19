package tripletail

sealed trait Command extends Product with Serializable

final case class Signup(email: String) extends Command

final case class Signin(license: String, email: String) extends Command