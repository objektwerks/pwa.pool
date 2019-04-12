package tripletail

final case class Fault(message: String, code: Int = 0)

object Fault {
  def toFault(statusCode: Int): Fault = {
    println(s"post http status code error: $statusCode")
    Fault(s"http status code: ${statusCode.toString}", statusCode)
  }

  def toFault(t: Throwable): Fault = {
    println(s"post method exception: ${t.printStackTrace()}")
    Fault(s"${t.getMessage}")
  }
}