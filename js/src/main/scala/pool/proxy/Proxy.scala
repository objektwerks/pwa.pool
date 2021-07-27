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
    Context.log(s"... in readAsFault(responseText: String) ... ${xhr.responseText}")
    Try(read[Fault](xhr.responseText)).getOrElse( log(xhr) )
  }

  def log(error: Throwable): Fault = {
    Context.log(s"... in log(error: Throwable) ... ${error.getMessage}")
    Context.log(error.printStackTrace())
    Fault(cause = error.getMessage)
  }

  def log(xhr: XMLHttpRequest): Fault = {
    Context.log(s"... in log(statusText: String) ... ${xhr.statusText}")
    val fault = Fault(cause = s"status: ${xhr.statusText} : response: ${xhr.responseText}")
    Context.log(fault.toString)
    fault
  }
}