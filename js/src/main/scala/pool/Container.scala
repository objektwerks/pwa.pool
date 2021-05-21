package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  import ServerProxy._

  def render(publicUrl: String, apiUrl: String): Div = {
    println(s"public url: $publicUrl")
    println(s"api url: $apiUrl")
    println(s"now url: $publicUrl/now")

    renderHomePage(publicUrl)
  }

  def renderHomePage(publicUrl: String): Div =
    div(
      renderHomeNavigation,
      div(
        renderNowLabel(publicUrl)
      )
    )

  def renderHomeNavigation: Div =
    div( 
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), cls("w3-bar-item w3-button"), "Register"),
      a( href("#"), cls("w3-bar-item w3-button"), "Login")
    )

  def renderNowLabel(publicUrl: String): Label = {
    val datetimeVar = Var("")
    get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    label(
      cls("w3-text-indigo"),
      fontSize("12px"),
      child.text <-- datetimeVar
    )
  }

  def renderRegisterPage(publicUrl: String): Div = div( idAttr("register"), publicUrl )

  def renderLoginPage(publicUrl: String): Div = div( idAttr("login"), publicUrl )
}