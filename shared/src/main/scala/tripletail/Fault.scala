package tripletail

import java.time.Instant

final case class Fault(message: String, code: Int = 0, occurred: Long = Instant.now.toEpochMilli)

object Fault {
  def apply(statusCode: Int): Fault = Fault(s"http status code: ${statusCode.toString}", statusCode)

  def apply(t: Throwable): Fault = Fault(s"${t.getMessage}")
}