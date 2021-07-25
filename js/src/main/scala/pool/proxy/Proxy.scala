package pool.proxy

import pool.{Account, Context, Fault, Serializers}

import scala.util.Try

abstract class Proxy {
  import Serializers._
  import upickle.default._

  val headers = Map(
    "Content-Type" -> "application/json; charset=utf-8",
    "Accept" -> "application/json"
  )

  def headers(license: String): Map[String, String] =
    if (license.nonEmpty) headers + ( Account.licenseHeader -> license )
    else headers


  def toFault(responseText: String): Left[Fault, Nothing] = {
    Context.log(s"*** in toFault(responseText: String) ... $responseText")
    Try(read[Fault](responseText)).fold(error => Left(log(error)), fault => Left(fault))
  }

  def log(error: Throwable): Fault = {
    Context.log(s"*** in log(error: Throwable) ... ${error.getMessage}")
    Context.log(error.printStackTrace())
    log(error.getMessage)
  }

  def log(statusText: String, statusCode: Int = 500): Fault = {
    Context.log(s"*** in log(statusText: String, statusCode: Int = 500) ... $statusText / $statusCode")
    val fault = Fault(code = statusCode, cause = statusText)
    Context.log(fault.toString)
    fault
  }
}