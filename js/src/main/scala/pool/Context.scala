package pool

import org.scalajs.dom.document

case class Context(urls: Urls, model: Model) extends Product with Serializable {
  def displayToBlock(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def displayToNone(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")
}