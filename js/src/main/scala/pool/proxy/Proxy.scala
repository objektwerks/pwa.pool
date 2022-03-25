package pool.proxy

import org.scalajs.dom.XMLHttpRequest

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

  def readAsFault(xhr: XMLHttpRequest): Fault = {
    Context.log(s"... in readAsFault(xhr: XMLHttpRequest) ... ${xhr.responseText}")
    Try(read[Fault](xhr.responseText)).getOrElse( log(xhr) )
  }

  def log(error: Throwable): Fault = {
    Context.log(s"... in log(error: Throwable) ... ${error.getMessage}")
    Context.log(error.printStackTrace())
    Fault(cause = s"Response handling error: ${error.getMessage}")
  }

  def log(xhr: XMLHttpRequest): Fault = {
    val error = s"Status: ${xhr.statusText} : Response: ${xhr.responseText}"
    Context.log(s"... in log(xhr: XMLHttpRequest) ... $error")
    val fault = xhr.status match {
      case 400 => Fault(cause = s"Request handling error. $error.")
      case 401 => Fault(cause = s"Unauthorized pin. $error.")
      case 500 => Fault(cause = s"Server error. $error.")
      case _ => Fault(cause = s"Unknown error. $error")
    }
    Context.log(fault)
    fault
  }
}