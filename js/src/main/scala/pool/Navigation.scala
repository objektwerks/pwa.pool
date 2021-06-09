package pool

import com.raquo.laminar.api.L._

import scala.annotation.nowarn

object Navigation {
  @nowarn def render(context: Context, model: Model): Div =
    div( cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => Register.render(context, model) ),
         "Register" ),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => Login.render(context, model) ),
         "Login" )
    )  
}