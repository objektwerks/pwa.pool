package poolmate

import com.raquo.laminar.api.L._

import org.scalajs.dom._
import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.scalajs.js
import scala.scalajs.js.annotation.JSExportTopLevel

@JSExportTopLevel("Client")
class Client(rootUrl: String) extends js.Object:
  Url.root = rootUrl
  Url.now = s"${Url.root}/now"
  Url.command = s"${Url.root}/command"

  log(s"root url: ${Url.root}")
  log(s"now url: ${Url.now}")
  log(s"command url: ${Url.command}")

  Proxy.now.foreach( now => log(s"[now] $now") )

  render(
    container = document.getElementById("container"),
    rootNode = div( child <-- PageRouter.splitter.$view )
  )