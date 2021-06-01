package pool

import com.raquo.laminar.api.L._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.annotation.nowarn

@nowarn object CommandObserver {
  def apply(apiUrl: String,
            serverProxy: ServerProxy,
            eventObserver: EventObserver): Observer[Command] = Observer[Command] {
    case signup: SignUp => 
      println(s"signup $apiUrl/signup with $signup ...")
      serverProxy.post(s"$apiUrl/signup", "", signup).map { either =>
        either match {
          case Right(event) => eventObserver.handle(event)
          case Left(fault) => println(s"signup $apiUrl/signup to $fault ...")
        }
      }

    case signin: SignIn => println(s"signin $apiUrl/signin with $signin ...")
    case deactivate: DeactivateLicensee => println(s"deactivate license $apiUrl/deactivatelicensee with $deactivate ...")
    case reactivate: ReactivateLicensee => println(s"reactivated licensee $apiUrl/reactivatelicensee with $reactivate ...")
  }
}