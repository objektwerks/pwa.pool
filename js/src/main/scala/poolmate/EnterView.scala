package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Error.*
import Message.*
import Validators.*

object EnterView extends View:
  def apply(pinVar: Var[String], accountVar: Var[Account]): HtmlElement =
    val pinErrorBus = new EventBus[String]

    def handler(event: Either[Fault, Event]): Unit =
      event match
        case Right(event) =>
          event match
            case LoggedIn(account) =>
              clearErrors()
              accountVar.set(account)
              route(HomePage)
            case _ => log(s"Enter view handler failed: $event")
        case Left(fault) => errorBus.emit(s"Enter view handler failed: ${fault.cause}")
      
    div(      
      hdr("Login"),
      info(enterMessage),
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
          disabled <-- pinVar.signal.map( pin => !pin.isPin )
          onClick --> { _ =>
            log(s"Login onClick -> pin: ${pinVar.now()}")
            val command = Enter(pinVar.now())
            call(command, handler)
          }
        }
      )
    )