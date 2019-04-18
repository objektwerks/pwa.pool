package tripletail

import java.time.Instant

final case class Fault(message: String, code: Int = 0, occurred: Long = Instant.now.toEpochMilli)

object Fault {
  def apply(statusText: String, statusCode: Int): Fault = Fault(statusText, statusCode)

  def apply(throwable: Throwable): Fault = Fault(s"${throwable.getMessage}")
}