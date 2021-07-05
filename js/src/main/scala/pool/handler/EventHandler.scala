package pool.handler

import com.raquo.laminar.api.L._
import org.scalajs.dom.console
import pool.{Context, Event, Fault, State}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object EventHandler {
  def handle(context: Context,
             errors: EventBus[String],
             response: Future[Either[Fault, Event]],
             handler: (Context, EventBus[String], Event) => Unit): Unit = {
    response.onComplete {
      case Success(either) => either match {
        case Right(event) =>
          console.debug(s"Success: $event")
          handler(context, errors, event)
        case Left(fault) =>
          console.error(s"Fault: $fault")
          errors.emit(s"Fault: $fault")
      }
      case Failure(failure) =>
        console.error(s"Failure: $failure")
        errors.emit(s"Failure: $failure")
    }
  }
}