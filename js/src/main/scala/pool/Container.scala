package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  import ServerProxy._

  def render(publicUrl: String, apiUrl: String): Div = {
    println(s"public url: $publicUrl")
    println(s"api url: $apiUrl")
    println(s"now url: $publicUrl/now")

    div(
      cls("w3-container w3-white"),
      renderNow(publicUrl)
    )
  }

  def renderNow(publicUrl: String): Label = {
    val datetimeVar = Var("")
    get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    label(
      fontSize("11px"),
      color("indigo"),
      child.text <-- datetimeVar
    )
  }
}