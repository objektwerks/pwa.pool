package pool

import com.raquo.laminar.api.L._

case class Model(email: Var[String] = Var(""),
                 pin: Var[Int] = Var(0),
                 license: Var[String] = Var(""),
                 licensee: Var[Option[Licensee]] = Var(None)) extends Product with Serializable