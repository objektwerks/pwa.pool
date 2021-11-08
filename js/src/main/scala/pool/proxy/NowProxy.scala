package pool.proxy

import org.scalajs.dom
import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.AjaxException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import scala.scalajs.js.Thenable.Implicits._

object NowProxy extends Proxy {
  def post(url: String): Future[String] =
    Ajax.post(url = url, headers = headers).map { xhr =>
      xhr.status match {
        case 200 => xhr.responseText.stripPrefix("\"").stripSuffix("\"")
        case _ => xhr.statusText
      }
    }.recover { case AjaxException(xhr) => xhr.statusText }

  def fetch(url: String): Future[String] = {
    ( for {
      response <- dom.fetch(url)
      json     <- response.text()
      text     =  json.stripPrefix("\"").stripSuffix("\"")
    } yield {
      text
    } ).recover { case error: Exception => error.getMessage }
  }
}