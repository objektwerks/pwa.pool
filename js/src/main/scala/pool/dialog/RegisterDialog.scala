package pool.dialog

import com.raquo.laminar.api.L._
import pool.{Context, Licensee, ServerProxy, SignUp, SignedUp}

import scala.concurrent.ExecutionContext.Implicits.global

object RegisterDialog {
  def apply(context: Context): Div =
    div( idAttr("registerDialog"), cls("w3-modal"),
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
          div( cls("w3-row w3-padding-16"),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> {_ =>
                val command = SignUp(context.model.email.now())
                ServerProxy.post(context.urls.signup, Licensee.emptyLicense, command).foreach {
                  case Right(event) => event match {
                    case signedup: SignedUp =>
                      println(s"signedup $signedup")
                      context.model.licensee.set( Some(signedup.licensee) )
                      context.displayToNone("registerDialog")
                    case _ => println(s"wrong event: $event")
                  }
                  case Left(fault) => println(s"fault: $fault")
                }
               },
              "Register"
            ),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> (_ => context.displayToNone("registerDialog") ),
              "Cancel"
            )
          )
        )
      )
    )
}