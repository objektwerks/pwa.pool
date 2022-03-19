package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Error.*
import Message.*
import Validators.*

object LoginView extends View:
  def apply(emailAddressVar: Var[String], pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    val emailAddressErrorBus = new EventBus[String]
    val pinErrorBus = new EventBus[String]

    def handler(event: Either[Fault, Event]): Unit =
      event match
        case Right(event) =>
          event match
            case LoggedIn(account) =>
              clearErrors()
              accountVar.set(account)
              route(HomePage)
            case _ => log(s"Login view handler failed: $event")
        case Left(fault) => errorBus.emit(s"Login failed: ${fault.cause}")
      
    div(      
      hdr("Login"),
      lbl("Email Address"),
      email.amend {
        value <-- emailAddressVar
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> emailAddressVar
        onKeyUp.mapToValue --> { emailAddress =>
          if emailAddress.isEmailAddress then clear(emailAddressErrorBus) else emit(emailAddressErrorBus, emailAddressError)
        }
      },
      err(emailAddressErrorBus),
      lbl("Pin"),
      pin.amend {
        value <-- pinVar
        onInput.mapToValue.filter(_.nonEmpty).setAsValue --> pinVar
        onKeyUp.mapToValue --> { pin =>
          if pin.isPin then clear(pinErrorBus) else emit(pinErrorBus, pinError)
        }      
      },
      info(pinMessage),
      err(pinErrorBus),
      cbar(
        btn("Login").amend {
          disabled <-- emailAddressVar.signal.combineWithFn(pinVar.signal) {
            (email, pin) => !(email.isEmailAddress && pin.isPin)
          }
          onClick --> { _ =>
            log(s"Login onClick -> email address: ${emailAddressVar.now()} pin: ${pinVar.now()}")
            val command = Login(emailAddressVar.now(), pinVar.now())
            call(command, handler)
          }
        }
      )
    )