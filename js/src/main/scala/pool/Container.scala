package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.annotation.nowarn

object Container {
  def apply(publicUrl: String, apiUrl: String, serverProxy: ServerProxy): Container = new Container(publicUrl, apiUrl, serverProxy)
}

class Container(publicUrl: String, apiUrl: String, serverProxy: ServerProxy) {
  println(s"public url: $publicUrl")
  println(s"api url: $apiUrl")

  def render: Div =
    div(
      renderNavigation,
      renderNow
    )

  @nowarn def renderNavigation: Div =
    div( 
      cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"), onClick --> (_ => renderRegister), cls("w3-bar-item w3-button"), "Register" ),
      a( href("#"), onClick --> (_ => renderLogin), cls("w3-bar-item w3-button"), "Login" )
    )

  def renderNow: Div = {
    val datetimeVar = Var("")
    serverProxy.get(s"$publicUrl/now").foreach( now => datetimeVar.set(now.stripPrefix("\"").stripSuffix("\"")) )
    div(
      label(
        cls("w3-text-indigo"),
        fontSize("12px"),
        child.text <-- datetimeVar
      )
    )
  }

  def renderRegister: Div =
    div(
      idAttr("register"),
      cls("w3-container"),
      label("Email Address:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") )
    )

  def renderLogin: Div =
    div(
      idAttr("login"),
      cls("w3-container"),
      label("Email Address:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") ),
      label("PIN:"),
      input( cls("w3-input w3-text-indigo"), tpe("text") )
    )
}