package pool.dialog

import com.raquo.laminar.api.L._

import pool.{Context, ReactivateLicensee, LicenseeReactivated, ServerProxy}

import scala.concurrent.ExecutionContext.Implicits.global

object ReactivateDialog {
  val id = getClass.getSimpleName
  val statusEvents = new EventBus[String]

  def apply(context: Context): Div =
    div( idAttr(id), cls("w3-modal"),
      div( cls("w3-container"),
        div( cls("w3-modal-content"),
          div( cls("w3-panel w3-indigo"),
            label( cls("w3-left-align"), "Status:" ),
            child.text <-- statusEvents.events.toSignal("")
          ),
          div( cls("w3-row w3-margin"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "License:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"),
                minLength(36), maxLength(36), required(true), autoFocus(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.license
              )
            )
          ),
          div( cls("w3-row w3-margin"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Email:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("email"), required(true),
                onChange.mapToValue.filter(_.nonEmpty) --> context.email
              )
            )
          ),
          div( cls("w3-row w3-margin"),
            div( cls("w3-col"), width("15%"),
              label( cls("w3-left-align w3-text-indigo"), "Pin:" )
            ),
            div( cls("w3-col"), width("85%"),
              input( cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
                onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.pin
              )
            )
          ),
          div( cls("w3-row w3-margin"),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> {_ =>
                val command = ReactivateLicensee(context.license.now(), context.email.now(), context.pin.now())
                ServerProxy.post(context.reactivateUrl, command.license, command).foreach {
                  case Right(event) => event match {
                    case reactivated: LicenseeReactivated =>
                      statusEvents.emit( s"Success: $reactivated" )
                      context.licensee.set( Some(reactivated.licensee) )
                      context.displayToNone(id)
                    case _ => statusEvents.emit( s"Invalid: $event" )
                  }
                  case Left(fault) => statusEvents.emit( s"Failure: $fault" )
                }
              },
              "Reactivate"
            ),
            button( cls("w3-btn w3-text-indigo"),
              onClick --> (_ => context.displayToNone(id) ),
              "Cancel"
            )
          )
        )
      )
    )
}