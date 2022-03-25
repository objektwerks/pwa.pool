package pool

final case class Fault(dateOf: Int,
                       timeOf: Int,
                       nanoOf: Int,
                       code: Int,
                       cause: String) extends Product with Serializable

object Fault {
  def apply(code: Int, cause: String): Fault = Fault(
    dateOf = DateTime.currentDate,
    timeOf = DateTime.currentTime,
    nanoOf = DateTime.nano,
    code = code,
    cause = cause
  )

  def apply(cause: String): Fault = Fault(
    dateOf = 0,
    timeOf = 0,
    nanoOf = 0,
    code = 0,
    cause = cause
  )
}