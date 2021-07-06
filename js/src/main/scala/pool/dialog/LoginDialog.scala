package pool.dialog

import com.raquo.laminar.api.L._

import pool.menu.HomeMenu
import pool.proxy.CommandProxy
import pool.{Account, Context, LoggedIn, Login}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object LoginDialog {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def apply(context: Context): Div =
    div(idAttr(id), cls("w3-modal"),
      div(cls("w3-container"),
        div(cls("w3-modal-content"),
          div(cls("w3-container w3-indigo"),
            h6("Login")
          ),
          div(cls("w3-panel w3-red"),
            child.text <-- errors.events
          ),
          div(cls("w3-row w3-margin"),
            div(cls("w3-col"), width("15%"),
              label(cls("w3-left-align w3-text-indigo"), "Pin:")
            ),
            div(cls("w3-col"), width("85%"),
              input(cls("w3-input w3-hover-light-gray w3-text-indigo"), typ("number"), required(true),
                onChange.mapToValue.filter(_.toIntOption.nonEmpty).map(_.toInt) --> context.pin
              )
            )
          ),
          div(cls("w3-bar"),
            button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"),
              onClick --> { _ =>
                val command = Login(context.pin.now())
                println(s"Command: $command")
                CommandProxy.post(context.loginUrl, Account.emptyLicense, command).onComplete {
                  case Success(either) => either match {
                    case Right(event) => event match {
                      case loggedin: LoggedIn =>
                        println(s"Success: $event")
                        context.account.set(loggedin.account)
                        context.hide(HomeMenu.registerMenuItemId)
                        context.hide(HomeMenu.loginMenuItemId)
                        context.show(HomeMenu.accountMenuItemId)
                        context.show(HomeMenu.poolsMenuItemId)
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
              "Login"
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