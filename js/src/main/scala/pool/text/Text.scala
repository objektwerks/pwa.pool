package pool.text

import com.raquo.laminar.api.L._

object Text {
  def field(typeOf: String): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true)
    )

  def readonly(typeOf: String = "text"): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true),
      readOnly(true)
    )

  def text(textVar: Var[String], typeOf: String = "text", isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ(typeOf),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.nonEmpty) --> textVar
    )

  def integer(integerVar: Var[Int], isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ("number"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> integerVar
    )

  def double(doubleVar: Var[Double], isReadOnly: Boolean = false): Input =
    input(
      cls("w3-input w3-hover-light-gray w3-text-indigo"),
      typ("number"),
      required(true),
      readOnly(isReadOnly),
      onInput.mapToValue.filter(_.toDoubleOption.nonEmpty).map(_.toDouble) --> doubleVar
    )
}