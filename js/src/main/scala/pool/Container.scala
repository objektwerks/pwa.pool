package pool

import com.raquo.laminar.api.L._

object Container {
  def apply(serverProxy: ServerProxy, apiUrl: String): HtmlElement = {
    println(serverProxy)
    println(apiUrl)
    div( p( "Todo!") )
  }
}