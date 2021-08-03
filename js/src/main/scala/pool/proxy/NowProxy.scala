package pool.proxy

import org.scalajs.dom.ext.Ajax
import org.scalajs.dom.ext.AjaxException

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object NowProxy extends Proxy {
  def post(url: String): Future[String] =
    Ajax.post(url = url, headers = headers).map { xhr =>
      xhr.status match {
        case 200 => xhr.responseText.stripPrefix("\"").stripSuffix("\"")
        case _ => xhr.statusText
      }
    }.recover { case AjaxException(xhr) => xhr.statusText }
}