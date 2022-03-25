package pool.text

import com.raquo.laminar.api.L._

object Label {
  def apply(name: String): Label = label(cls("w3-left-align w3-text-indigo"), name)
}