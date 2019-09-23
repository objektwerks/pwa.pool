package tripletail

final case class Fault(message: String,
                       code: Int = 500,
                       dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       nanoOf: Long = System.nanoTime)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(message = statusText, code = statusCode)

  def apply(throwable: Throwable): Fault = Fault(message = s"${throwable.getMessage}")
}