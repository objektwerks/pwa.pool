package tripletail

trait Validator[T] {
  def validate(entity: T): Boolean
}

object SignupValidator {
  val validator = new Validator[Signup] {
    override def validate(signup: Signup): Boolean = {
      import StringValidators._
      signup.email.nonNullEmpty
    }
  }
  implicit class Methods(val signup: Signup) {
    def isValid: Boolean = validator.validate(signup)
  }
}

object SigninValidator {
  val validator = new Validator[Signin] {
    override def validate(signin: Signin): Boolean = {
      import StringValidators._
      signin.email.nonNullEmpty
    }
  }
  implicit class Methods(val signin: Signin) {
    def isValid: Boolean = validator.validate(signin)
  }
}

object LicenseeValidator {
  val validator = new Validator[Licensee] {
    override def validate(licensee: Licensee): Boolean = {
      import IntValidators._
      import StringValidators._
      licensee.license.nonNullEmpty &&
        licensee.email.nonNullEmpty &&
        licensee.activated.greaterThanEqual(0)
    }
  }
  implicit class Methods(val licensee: Licensee) {
    def isValid: Boolean = validator.validate(licensee)
  }
}