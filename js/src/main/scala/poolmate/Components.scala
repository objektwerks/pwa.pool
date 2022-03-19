package poolmate

import com.raquo.laminar.api.L.*

import scala.scalajs.js.Date

object Components:
  private val inputCss = "w3-input w3-hover-light-gray w3-text-indigo"
  private val currentYear = new Date().getFullYear().toInt.toString
  // Calling DateTime causes this error: unknown timezone id: america/new york

  def bar(elms: HtmlElement*): Div =
    div(cls("w3-bar"), elms)

  def cbar(elms: HtmlElement*): Div =
    div(cls("w3-bar w3-margin-top w3-center"), elms)

  def btn(text: String): Button =
    button(cls("w3-button w3-round-xxlarge w3-mobile w3-light-grey w3-text-indigo"), text)

  def rbtn(text: String): Button =
    button(cls("w3-button w3-round-xxlarge w3-mobile w3-light-grey w3-text-indigo w3-right"), text)
  
  def dropdown(header: Button, buttons: Button*): Div =
    div(cls("w3-dropdown-hover"), header, div(cls("w3-dropdown-content w3-bar-block w3-card-4"), buttons))

  def lbl(text: String): Label =
    label(cls("w3-left-align w3-text-indigo"), text)

  def info(text: String): Div =
    div(cls("w3-border-white w3-text-indigo"), b(text))

  def txt: Input =
    input(cls(inputCss), required(true))

  def rotxt: Input =
    input(cls(inputCss), readOnly(true))

  def email: Input =
    input(cls(inputCss), typ("email"), minLength(3), placeholder("address@email.com"), required(true))

  def pin: Input =
    input(cls(inputCss), typ("text"), minLength(6), maxLength(6), required(true), placeholder("a$b2c#"))

  def year: Input =
    input(
      cls(inputCss), typ("number"), pattern("\\d*"),
      stepAttr("1"), minAttr("1900"), maxAttr(currentYear),
      placeholder(currentYear), required(true)
    )

  def date: Input =
    input(cls(inputCss), tpe("date"), required(true))
 
  def time: Input =
    input(cls(inputCss), tpe("time"), required(true))

  def int: Input =
    input(cls(inputCss), typ("number"), pattern("\\d*"), stepAttr("1"), required(true))

  def dbl: Input =
    input(cls(inputCss), typ("number"), pattern("[0-9]+([.,][0-9]+)?"), stepAttr("0.01"), required(true))

  def hdr(text: String): HtmlElement =
    h5(cls("w3-light-grey w3-text-indigo"), text)

  def err(errBus: EventBus[String]): Div =
    div(cls("w3-border-white w3-text-red"), child.text <-- errBus.events)

  def msg(noteBus: EventBus[String]): Div =
    div(cls("w3-border-white w3-text-indigo"), child.text <-- noteBus.events)

  def list(liSignal: Signal[Seq[Li]]): Div =
    div(cls("w3-container"), ul(cls("w3-ul w3-hoverable"), children <-- liSignal))

  def item(strSignal: Signal[String]): Li =
    li(cls("w3-text-indigo w3-display-container"), child.text <-- strSignal)

  def split[E <: Entity](entities: Var[Seq[E]], toEntityPage: Long => EntityPage): Signal[Seq[Li]] =
    entities.signal.split(_.id)( (id, _, entitySignal) =>
      item( entitySignal.map(_.display) ).amend {
        onClick --> { _ =>
          entities.now().find(_.id == id).foreach { entity =>
            PageRouter.router.pushState(toEntityPage(id))
          }
        }
      }
    )