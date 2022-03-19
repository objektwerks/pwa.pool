package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

import Components.*
import Error.*
import Message.*
import Validators.*

object RegisterView extends View:
  def apply(emailAddressVar: Var[String], pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    val emailAddressErrorBus = new EventBus[String]

    def handler(event: Either[Fault, Event]): Unit =
      event match
        case Right(event) =>
          event match
            case Registered(account) =>
              clearErrors()
              accountVar.set(account)
              pinVar.set(account.pin)
              route(LoginPage)
            case _ => log(s"Register view handler failed: $event")
        case Left(fault) => errorBus.emit(s"Register failed: ${fault.cause}")
      
    div(
      hdr("Register"),
      info(registerMessage),
      err(errorBus),
      lbl("Email Address"),
      email.amend {
        value <-- emailAddressVar
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> emailAddressVar
        onKeyUp.mapToValue --> { emailAddress =>
          if emailAddress.isEmailAddress then clear(emailAddressErrorBus) else emit(emailAddressErrorBus, emailAddressError)
        }
      },
      err(emailAddressErrorBus),
      cbar(
        btn("Register").amend {
          disabled <-- emailAddressVar.signal.map(email => !email.isEmailAddress)
          onClick --> { _ =>
            log(s"Register onClick -> email address: ${emailAddressVar.now()}")
            val command = Register(emailAddressVar.now())
            call(command, handler)
          }
        },
      )
    )