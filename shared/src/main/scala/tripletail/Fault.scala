package tripletail

final case class Fault(cause: String,
                       code: Int = 500,
                       dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       nanoOf: Long = System.nanoTime)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(cause = statusText, code = statusCode)

  def apply(throwable: Throwable): Fault = Fault(cause = s"${throwable.getMessage}")
}