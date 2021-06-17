package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom.document

case class Context(publicUrl: String, apiUrl: String) extends Product with Serializable {
  val email: Var[String] = Var("")
  val pin: Var[Int] = Var(0)
  val license: Var[String] = Var("")
  val licensee: Var[Option[Licensee]] = Var(None)

  val signupUrl = s"$publicUrl/signup"
  val signinUrl = s"$publicUrl/signin"
  val deactivateUrl = s"$publicUrl/deactivatelicensee"
  val reactivateUrl = s"$publicUrl/reactivatelicensee"
  val poolsUrl = s"$apiUrl/pools"
  val poolsAddUrl = s"$apiUrl/pools/add"
  val poolsUpdateUrl = s"$apiUrl/pools/update"

  def displayToBlock(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def displayToNone(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")
}