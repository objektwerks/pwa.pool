package pool.proxy

import org.scalajs.dom.console

import pool.{Account, Fault, Serializers}

import scala.util.Try

abstract class Proxy {
  import Serializers._
  import upickle.default._

  val headers = Map(
    "Content-Type" -> "application/json; charset=utf-8",
    "Accept" -> "application/json"
  )

  def headers(license: String): Map[String, String] =
    if (license.nonEmpty) headers + ( Account.headerLicenseKey -> license )
    else headers


  def toFault(responseText: String): Left[Fault, Nothing] =
    Try(read[Fault](responseText)).fold(error => Left(log(error)), fault => Left(fault))

  def log(error: Throwable): Fault = {
    console.error(error.printStackTrace())
    log(error.getMessage)
  }

  def log(statusText: String, status: Int = 500): Fault = {
    val fault = Fault(code = status, cause = statusText)
    console.error(fault.toString)
    fault
  }
}