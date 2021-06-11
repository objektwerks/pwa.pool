package pool

import com.raquo.laminar.api.L._

object Model {
  def apply(): Model = new Model(email = Var(""), pin = Var(0), license = Var(""))
}

case class Model(email: Var[String], pin: Var[Int], license: Var[String]) extends Product with Serializable