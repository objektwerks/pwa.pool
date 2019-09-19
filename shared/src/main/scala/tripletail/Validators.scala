package tripletail

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

import scala.util.Try

object StringValidators {
  val dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

  implicit class Methods(val value: String) {
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
}

object IntValidators {
  implicit class Methods(val value: Int) {
    def inRange(range: Range): Boolean = range.contains(value)

    def lessThan(integer: Int): Boolean = value < integer

    def lessThanEqual(integer: Int): Boolean = value <= integer

    def equal(integer: Int): Boolean = value == integer

    def greaterThan(integer: Int): Boolean = value > integer

    def greaterThanEqual(integer: Int): Boolean = value >= integer
  }
}