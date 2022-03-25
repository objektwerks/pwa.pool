package pool.text

import com.raquo.laminar.api.L._

object Text {
  val css = "w3-input w3-hover-light-gray w3-text-indigo"

  def field(typeOf: String = "text"): Input =
    input(
      cls(css),
      typ(typeOf),
      required(true)
    )

  def text(): Input =
    input(
      cls(css),
      typ("text"),
      required(true)
    )

  def text(textVar: Var[String], typeOf: String = "text", isReadOnly: Boolean = false): Input =
    input(
      cls(css),
      typ(typeOf),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.nonEmpty) --> textVar
    )

  def readonly(typeOf: String = "text"): Input =
    input(
      cls(css),
      typ(typeOf),
      required(true),
      readOnly(true)
    )

  def email(emailVar: Var[String]): Input =
    input(
      cls(css),
      typ("email"),
      required(true),
      onInput.mapToValue.filter(_.nonEmpty) --> emailVar
    )

  def integer(): Input =
    input(
      cls(css),
      typ("number"),
      pattern("\\d*"),
      stepAttr("1"),
      required(true)
    )

  def integer(integerVar: Var[Int], isReadOnly: Boolean = false): Input =
    input(
      cls(css),
      typ("number"),
      pattern("\\d*"),
      stepAttr("1"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> integerVar
    )

  def double(): Input =
    input(
      cls(css),
      typ("number"),
      pattern("[0-9]+([.,][0-9]+)?"),
      stepAttr("0.01"),
      required(true)
    )

  def double(doubleVar: Var[Double], isReadOnly: Boolean = false): Input =
    input(
      cls(css),
      typ("number"),
      pattern("[0-9]+([.,][0-9]+)?"),
      stepAttr("0.01"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> doubleVar
    )
}