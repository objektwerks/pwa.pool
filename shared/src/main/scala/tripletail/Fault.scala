package tripletail

final case class Fault(message: String, code: Int = 0)

object Fault {
  def apply(statusCode: Int): Fault = Fault(s"http status code: ${statusCode.toString}", statusCode)

  def apply(t: Throwable): Fault = Fault(s"${t.getMessage}")
}