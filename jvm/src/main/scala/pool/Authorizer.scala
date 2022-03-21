package pool

final class Authorizer(service: Service):
  def authorize(command: Command): Event =
    command match
      case license: License => service.authorize(license.license)
      case Register(_) | Login(_, _) => Authorized("")