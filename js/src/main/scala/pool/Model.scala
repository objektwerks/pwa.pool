package pool

import com.raquo.laminar.api.L._

object Model {
  def apply(): Model = new Model()
}

class Model {
  val email = Var("")
  val pin = Var(0)
  val license = Var("")
}