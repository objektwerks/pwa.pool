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
  def isNotNull(value: String): Boolean = null != value

  def isNotEmpty(value: String): Boolean = value.nonEmpty

  def isNotNullEmpty(value: String): Boolean = isNotNull(value) && isNotEmpty(value)

  def isLengthLessThan(value: String, length: Int): Boolean = if (isNotNullEmpty(value)) value.length < length else false

  def isLengthLessThanEqual(value: String, length: Int): Boolean = if (isNotNullEmpty(value)) value.length <= length else false

  def isLengthEqual(value: String, length: Int): Boolean = if (isNotNullEmpty(value)) value.length == length else false

  def isLengthGreaterThan(value: String, length: Int): Boolean = if (isNotNullEmpty(value)) value.length > length else false

  def isLengthGreaterThanEqual(value: String, length: Int): Boolean = if (isNotNullEmpty(value)) value.length >= length else false

  def isLessThan(value: Int, integer: Int): Boolean = value < integer

  def isLessThanEqual(value: Int, integer: Int): Boolean = value <= integer

  def isEqual(value: Int, integer: Int): Boolean = value == integer

  def isGreaterThan(value: Int, integer: Int): Boolean = value > integer

  def isGreaterThanEqual(value: Int, integer: Int): Boolean = value >= integer

}