package pool

import com.raquo.laminar.api.L._

import org.scalajs.dom._

object Register {
  def render(context: Context, model: Model): Div =
    div( idAttr("register"), cls("w3-modal"), display("block"),
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
        div( cls("w3-row w3-padding-16"),
          button( cls("w3-btn w3-text-indigo"),
            onClick.mapTo {
              document.getElementById("register").setAttribute("style", "display: none")
              SignUp(model.email.now()) 
             } --> context.commandObserver,
            "Register"
          ),
          button( cls("w3-btn w3-text-indigo"),
            onClick --> (_ => document.getElementById("register").setAttribute("style", "display: none") ),
            "Cancel"
          )
        )
      )
    )
}