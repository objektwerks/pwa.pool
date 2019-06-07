package tripletail

final case class Fault(message: String,
                       code: Int = 500,
                       occurred: String = DateTime.currentDate,
                       at: String = DateTime.currentTime)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(statusText, statusCode)

  def apply(throwable: Throwable): Fault = Fault(s"${throwable.getMessage}")
}