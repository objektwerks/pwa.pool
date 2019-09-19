package tripletail

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

import scala.util.Try

object Validators {
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  implicit class StringOps(val value: String) {
    def nonNull: Boolean = null != value

    def nonNullEmpty: Boolean = (null != value) && value.nonEmpty

    def lengthLessThan(length: Int): Boolean = if (nonNullEmpty) value.length < length else false

    def lengthLessThanEqual(length: Int): Boolean = if (nonNullEmpty) value.length <= length else false

    def lengthEqual(length: Int): Boolean = if (nonNullEmpty) value.length == length else false

    def lengthGreaterThan(length: Int): Boolean = if (nonNullEmpty) value.length > length else false

    def lengthGreaterThanEqual(length: Int): Boolean = if (nonNullEmpty) value.length >= length else false

    def isDate: Boolean = Try(LocalDate.parse(value, dateFormatter)).isSuccess

    def isTime: Boolean = Try(LocalTime.parse(value, timeFormatter)).isSuccess

    def isMoney: Boolean = Try(value.toDouble).isSuccess
  }

  implicit class IntOps(val value: Int) {
    def inRange(range: Range): Boolean = range.contains(value)

    def lessThan(integer: Int): Boolean = value < integer

    def lessThanEqual(integer: Int): Boolean = value <= integer

    def equal(integer: Int): Boolean = value == integer

    def greaterThan(integer: Int): Boolean = value > integer

    def greaterThanEqual(integer: Int): Boolean = value >= integer
  }
}

object SignupValidator {
  implicit class Ops(val signup: Signup) {
    import Validators._
    private val validator = new Validator[Signup] {
      override def isValid(signup: Signup): Boolean = {
        signup.email.nonNullEmpty
      }
    }
    def isValid: Boolean = validator.isValid(signup)
  }
}

object SigninValidator {
  implicit class Ops(val signin: Signin) {
    import Validators._
    private val validator = new Validator[Signin] {
      override def isValid(signin: Signin): Boolean = {
        signin.email.nonNullEmpty
      }
    }
    def isValid: Boolean = validator.isValid(signin)
  }
}

object LicenseeValidator {
  implicit class Ops(val licensee: Licensee) {
    import Validators._
    private val validator = new Validator[Licensee] {
      override def isValid(licensee: Licensee): Boolean = {
        licensee.license.nonNullEmpty &&
          licensee.email.nonNullEmpty &&
          licensee.activated.greaterThanEqual(0)
      }
    }
    def isValid: Boolean = validator.isValid(licensee)
  }
}