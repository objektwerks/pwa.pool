package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  import ServerProxy._

  def render(publicUrl: String, apiUrl: String): Div = {
    println(s"public url: $publicUrl")
    println(s"api url: $apiUrl")
    println(s"now url: $publicUrl/now")

    renderHome(publicUrl)
  }

  def renderHome(publicUrl: String): Div =
    div(
      renderHomeNavigation,
      div(
        renderNow(publicUrl)
      )
    )

  def renderHomeNavigation: Div =
    div( 
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), cls("w3-bar-item w3-button"), "Register"),
      a( href("#"), cls("w3-bar-item w3-button"), "Login")
    )

  def renderNow(publicUrl: String): Label = {
    val datetimeVar = Var("")
    get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    label(
      cls("w3-text-indigo"),
      fontSize("12px"),
      child.text <-- datetimeVar
    )
  }
}