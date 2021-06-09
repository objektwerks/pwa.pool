package pool

import com.raquo.laminar.api.L._

object Model {
  def apply(): Model = new Model(email = Var(""), pin = Var(0))
}

case class Model(email: Var[String], pin: Var[Int]) extends Product with Serializable