package pool.dialog

import com.raquo.laminar.api.L._

import pool.menu.HomeMenu
import pool.{Context, DeactivateLicensee, LicenseeDeactivated, LicenseeReactivated, ReactivateLicensee, ServerProxy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object AccountDialog {
  val id = getClass.getSimpleName
  val deactivateButtonId = id + "-deactivate-button"
  val reactivateButtonId = id + "-reactivate-button"
  val errors = new EventBus[String]

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          div(cls("w3-container w3-indigo"),
            h6("Account")
          ),
          div(cls("w3-panel w3-red"),
            child.text <-- errors.events
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "License:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.licensee.signal.map(_.license))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Email:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.licensee.signal.map(_.email))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Pin:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.licensee.signal.map(_.pin.toString))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Activated:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.licensee.signal.map(_.activated.toString))
            )
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("25%"),
              label(cls("w3-left-align w3-text-indigo"), "Deactivated:")
            ),
            div(cls("w3-col"), width("75%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("text"), readOnly(true),
                value <-- context.licensee.signal.map(_.deactivated.toString))
            )
          ),
          div(cls("w3-bar"),
            button(idAttr(deactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = DeactivateLicensee(context.licensee.now().license)
                println(s"Command: $command")
                ServerProxy.post(context.deactivateUrl, command.license, command).onComplete {
                  case Success(either) => either match {
                    case Right(event) => event match {
                      case deactivated: LicenseeDeactivated =>
                        println(s"Success: $event")
                        context.licensee.set(deactivated.licensee)
                        context.hide(HomeMenu.accountMenuItemId)
                        context.hide(id)
                      case _ => errors.emit(s"Invalid: $event")
                    }
                    case Left(fault) =>
                      println(s"Fault: $fault")
                      errors.emit(s"Fault: $fault")
                  }
                  case Failure(failure) =>
                    println(s"Failure: $failure")
                    errors.emit(s"Failure: $failure")
                }
              },
              "Deactivate"
            ),
            button(idAttr(reactivateButtonId), cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = ReactivateLicensee(context.licensee.now().license)
                println(s"Command: $command")
                ServerProxy.post(context.reactivateUrl, command.license, command).onComplete {
                  case Success(either) => either match {
                    case Right(event) => event match {
                      case reactivated: LicenseeReactivated =>
                        println(s"Success: $event")
                        context.licensee.set(reactivated.licensee)
                        context.hide(HomeMenu.accountMenuItemId)
                        context.hide(id)
                      case _ => errors.emit(s"Invalid: $event")
                    }
                    case Left(fault) =>
                      println(s"Fault: $fault")
                      errors.emit(s"Fault: $fault")
                  }
                  case Failure(failure) =>
                    println(s"Failure: $failure")
                    errors.emit(s"Failure: $failure")
                }
              },
              "Reactivate"
            ),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> (_ => context.hide(id)),
              "Cancel"
            )
          )
        )
      )
    )
}