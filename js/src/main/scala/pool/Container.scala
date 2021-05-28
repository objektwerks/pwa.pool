package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global

object Container {
  def apply(publicUrl: String, apiUrl: String, serverProxy: ServerProxy): Container = new Container(publicUrl, apiUrl, serverProxy)
}

class Container(publicUrl: String, apiUrl: String, serverProxy: ServerProxy) {
  println(s"public url: $publicUrl")
  println(s"api url: $apiUrl")

  def render: Div = {
    renderHomePage
  }

  def renderHomePage: Div =
    div(
      renderHomeNavigation,
      div(
        renderNowLabel
      )
    )

  def renderHomeNavigation: Div =
    div( 
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), cls("w3-bar-item w3-button"), "Register"),
      a( href("#"), cls("w3-bar-item w3-button"), "Login")
    )

  def renderNowLabel: Label = {
    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    label(
      cls("w3-text-indigo"),
      fontSize("12px"),
      child.text <-- datetimeVar
    )
  }

  def renderRegisterPage: Div = div( idAttr("register"), publicUrl )

  def renderLoginPage: Div = div( idAttr("login"), publicUrl )
}