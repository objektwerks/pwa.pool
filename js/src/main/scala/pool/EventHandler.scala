package pool

object EventHandler {
  def handle(event: Event): Unit = event match {
    case signedup: SignedUp => println(s"signedup $signedup")
    case signedin: SignedIn => println(s"signedin $signedin")
    case deactivated: LicenseeDeactivated => println(s"licensee deactivated $deactivated")
    case reactivated: LicenseeReactivated => println(s"licensee reactivated $reactivated")
  }
}