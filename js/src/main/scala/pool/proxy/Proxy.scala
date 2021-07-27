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


  def readAsFault(responseText: String): Fault = {
    Context.log(s"... in readAsFault(responseText: String) ... $responseText")
    Try(read[Fault](responseText)).getOrElse( log("empty", responseText) )
  }

  def log(error: Throwable): Fault = {
    Context.log(s"... in log(error: Throwable) ... ${error.getMessage}")
    Context.log(error.printStackTrace())
    Fault(cause = error.getMessage)
  }

  def log(statusText: String, responseText: String): Fault = {
    Context.log(s"... in log(statusText: String) ... $statusText")
    val fault = Fault(cause = s"status: $statusText : response: $responseText")
    Context.log(fault.toString)
    fault
  }
}