package tripletail

final case class Fault(message: String,
                       code: Int = 500,
                       occurred: Int = DateTime.currentDate,
                       at: Int = DateTime.currentTime)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(statusText, statusCode)

  def apply(throwable: Throwable): Fault = Fault(s"${throwable.getMessage}")
}