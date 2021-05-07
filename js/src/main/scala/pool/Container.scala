package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  def apply(serverProxy: ServerProxy, publicUrl: String, apiUrl: String): HtmlElement = {
    println(s"public url: $publicUrl")
    println(s"apil url: $apiUrl")
    println(s"now url: $publicUrl/now")

    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now) )
    div(
      child.text <-- datetimeVar
    )
  }
}