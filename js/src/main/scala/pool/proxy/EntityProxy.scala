package pool.proxy

import org.scalajs.dom.ext.{Ajax, AjaxException}

import pool.{Context, Entity, Fault, Serializers, State}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.Try

object EntityProxy extends Proxy {
  import Serializers._
  import upickle.default._

  def post(url: String, license: String, entity: Entity): Future[Either[Fault, State]] = {
    Context.log(s"Entity: $entity")
    Ajax.post(url = url, headers = headers(license), data = write[Entity](entity)).map { xhr =>
      xhr.status match {
        case 200 => Try(read[State](xhr.responseText)).fold(error => Left(log(error)), state => Right(state))
        case _ => Left(log(xhr))
      }
    }.recover { case AjaxException(xhr) =>
      xhr.status match {
        case 400 | 401 | 500 => Left(readAsFault(xhr))
        case _ => Left(log(xhr))
      }
    }
  }
}