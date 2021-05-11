package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  def apply(serverProxy: ServerProxy, publicUrl: String, apiUrl: String): HtmlElement = {
    println(s"public url: $publicUrl")
    println(s"api url: $apiUrl")
    println(s"now url: $publicUrl/now")

    div(
      cls("w3-container w3-white"),
      renderNow(serverProxy, publicUrl)
    )
  }

  def renderNow(serverProxy: ServerProxy, publicUrl: String): Label = {
    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    label(
      fontSize("11px"),
      color("indigo"),
      child.text <-- datetimeVar
    )
  }
}