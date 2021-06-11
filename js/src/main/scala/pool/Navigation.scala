package pool

import com.raquo.laminar.api.L._

object Navigation {
  def render(register: Div, login: Div): Div =
    div( idAttr("navigation"), cls("w3-bar w3-white w3-text-indigo"),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => register.amend( display("block") ) ),
         "Register" ),
      a( href("#"),
         cls("w3-bar-item w3-button"),
         onClick --> (_ => login.amend( display("block") ) ),
         "Login" ),
      register,
      login
    )
}