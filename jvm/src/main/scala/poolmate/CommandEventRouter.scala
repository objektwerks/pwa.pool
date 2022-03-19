package poolmate

import cask.main.Routes
import cask.model.{Request, Response}
import com.typesafe.scalalogging.LazyLogging

import Serializers.given

import upickle.default.{read, write}

final class CommandEventRouter(dispatcher: Dispatcher, store: Store) extends Routes with LazyLogging:
  @cask.post("/command")
  def command(request: Request) =
    val command = read[Command](request.text())
    logger.debug(s"*** Command: $command")

    val event = dispatcher.dispatch(command)
    logger.debug(s"*** Event: $event")
    event match {
      case fault: Fault => store.addFault(fault)
      case _ =>
    }
    write[Event](event)

  initialize()