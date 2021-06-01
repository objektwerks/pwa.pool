package pool

object EventHandler {
  def handle(either: Either[Fault, Event]): Unit = either match {
    case Right(event) => handle(event)
    case Left(fault) => println(s"$fault")
  }

  private def handle(event: Event): Unit = event match {
    case signedup: SignedUp => println(s"signedup $signedup")
    case signedin: SignedIn => println(s"signedin $signedin")
    case deactivated: LicenseeDeactivated => println(s"licensee deactivated $deactivated")
    case reactivated: LicenseeReactivated => println(s"licensee reactivated $reactivated")
  }
}