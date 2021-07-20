package pool

import org.scalajs.dom.document

object Context {
  def log(any: Any): Unit = println(any)
}

case class Context(publicUrl: String, apiUrl: String) extends Product with Serializable {
  val registerUrl = s"$publicUrl/register"
  val loginUrl = s"$publicUrl/login"
  val deactivateUrl = s"$publicUrl/deactivate"
  val reactivateUrl = s"$publicUrl/reactivate"
  val poolsUrl = s"$apiUrl/pools"
  val poolsAddUrl = s"$apiUrl/pools/add"
  val poolsUpdateUrl = s"$apiUrl/pools/update"

  def log(any: Any): Unit = Context.log(any)

  def show(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def hide(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")
}