package pool.dialog

import com.raquo.laminar.api.L._

import org.scalajs.dom._

import pool.{Context, SignIn}

object LoginDialog {
  def render(context: Context): Div =
    div( idAttr("loginDialog"), cls("w3-modal"),
      div( cls("w3-container"),
        div( cls("w3-modal-content"),
          div( cls("w3-row"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Email:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.model.email
              )
            )
          ),
          div( cls("w3-row"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Pin:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
                onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.model.pin
              )
            )
          ),
          div( cls("w3-row w3-padding-16"),
            button( cls("w3-btn w3-text-indigo"),
              onClick.mapTo {
                document.getElementById("loginDialog").setAttribute("style", "display: none")
                SignIn(context.model.email.now(), context.model.pin.now())
              } --> context.commands,
              "Login"
            ),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> (_ => document.getElementById("loginDialog").setAttribute("style", "display: none") ),
              "Cancel"
            )
          )
        )
      )
    )  
}