package poolmate

final class Dispatcher(authorizer: Authorizer,
                       validator: Validator,
                       handler: Handler):
  def dispatch(command: Command): Event =
    authorizer.authorize(command) match
      case unauthorized: Unauthorized => unauthorized
      case _ => 
        if validator.isValid(command) then handler.handle(command)
        else Fault(s"Invalid command: $command")