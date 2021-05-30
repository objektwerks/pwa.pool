package pool

import com.raquo.laminar.api.L._

object CommandObserver {
  def apply(apiUrl: String): Observer[Command] = Observer[Command] {
    case signup: SignUp => println(s"signup $apiUrl/signup with $signup ...")
    case signin: SignIn => println(s"signin $apiUrl/signin with $signin ...")
    case deactivate: DeactivateLicensee => println(s"deactivate license $apiUrl/deactivatelicensee with $deactivate ...")
    case reactivate: ReactivateLicensee => println(s"reactivated licensee $apiUrl/reactivatelicensee with $reactivate ...")
  }
}