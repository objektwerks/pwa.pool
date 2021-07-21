package pool

final case class Fault(dateOf: Int,
                       timeOf: Int,
                       nanoOf: Int,
                       code: Int,
                       cause: String) extends Product with Serializable

object Fault {
  def apply(code: Int = 500, cause: String): Fault = Fault(
    dateOf = DateTime.currentDate,
    timeOf = DateTime.currentTime,
    nanoOf = DateTime.nano,
    code = code,
    cause = cause
  )
}