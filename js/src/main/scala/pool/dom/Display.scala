package pool.dom

import org.scalajs.dom.document

trait Display {
  def displayToBlock(id: String): Unit = document.getElementById(id).setAttribute("style", "display: block")

  def displayToNone(id: String): Unit = document.getElementById(id).setAttribute("style", "display: none")
}