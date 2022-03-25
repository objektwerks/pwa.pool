package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import pool.proxy.NowProxy

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(publicUrl: String, apiUrl: String) extends js.Object {
  val context = Context(publicUrl, apiUrl)

  NowProxy.fetch(s"$publicUrl/now").foreach(now => context.log(s"[now] $now"))

  ServiceWorker.register()

  render(document.getElementById("content"), context.content)
}