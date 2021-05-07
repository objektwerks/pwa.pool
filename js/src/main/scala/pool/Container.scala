package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  def apply(serverProxy: ServerProxy, apiUrl: String): HtmlElement = {
    val datetimeVar = Var("")
    serverProxy.get(s"$apiUrl/now").foreach( now => datetimeVar.set(now) )
    div(
      child.text <-- datetimeVar
    )
  }
}