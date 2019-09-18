package tripletail

/**
 * string - not null
 * string - non empty
 * string - not null and non empty
 * string length - lt, lte, eq, gt, gte
 * email - is valid via regex
 * number - lt, lte, eq, gt, gte
 * range - inclusive, exclusive
 * date - is valid
 * time - is valid
 * money - is valid
 */
object Validation {
  object StringValidation {
    implicit class Methods(val value: String) {
      def isNotNull: Boolean = null != value

      def isNotEmpty: Boolean = value.nonEmpty

      def isNotNullEmpty: Boolean = (null != value) && value.nonEmpty

      def isLengthLessThan(length: Int): Boolean = if (isNotNullEmpty) value.length < length else false

      def isLengthLessThanEqual(length: Int): Boolean = if (isNotNullEmpty) value.length <= length else false

      def isLengthEqual(length: Int): Boolean = if (isNotNullEmpty) value.length == length else false

      def isLengthGreaterThan(length: Int): Boolean = if (isNotNullEmpty) value.length > length else false

      def isLengthGreaterThanEqual(length: Int): Boolean = if (isNotNullEmpty) value.length >= length else false
    }
  }

  object IntValidation {
    implicit class Methods(val value: Int) {
      def isLessThan(integer: Int): Boolean = value < integer

      def isLessThanEqual(integer: Int): Boolean = value <= integer

      def isEqual(integer: Int): Boolean = value == integer

      def isGreaterThan(integer: Int): Boolean = value > integer

      def isGreaterThanEqual(integer: Int): Boolean = value >= integer
    }
  }
}