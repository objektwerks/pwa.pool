package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

object Login {
  def render(context: Context, model: Model): Div =
    div( idAttr("login"), cls("w3-modal"), display("block"),
      div( cls("w3-modal-content w3-card w3-animate-left"),
        div( cls("w3-row"),
          div( cls("w3-col"), width("15%"),
            label( cls("w3-left-align w3-text-indigo"), "Email:" )
          ),
          div( cls("w3-col"), width("85%"),
            input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
              onChange.mapToValue.filter(_.nonEmpty) --> model.email
            )
          )
        ),
        div( cls("w3-row"),
          div( cls("w3-col"), width("15%"),
            label( cls("w3-left-align w3-text-indigo"), "Pin:" )
          ),
          div( cls("w3-col"), width("85%"),
            input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
              onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> model.pin
            )
          )
        ),
        div( cls("w3-row w3-padding-16"),
          button( cls("w3-btn w3-text-indigo"),
            onClick.mapTo { 
              document.getElementById("login").setAttribute("style", "display: none")
              SignIn(model.email.now(), model.pin.now())
            } --> context.commandObserver,
            "Login"
          ),
          button( cls("w3-btn w3-text-indigo"),
            onClick --> (_ => document.getElementById("login").setAttribute("style", "display: none") ),
            "Cancel"
          )
        )
      )
    )  
}