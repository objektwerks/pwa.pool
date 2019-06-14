package tripletail

final case class Fault(id: Int = 0,
                       message: String,
                       code: Int = 500,
                       occurred: Int = DateTime.currentDate,
                       at: Int = DateTime.currentTime)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(message = statusText, code = statusCode)

  def apply(throwable: Throwable): Fault = Fault(message = s"${throwable.getMessage}")
}