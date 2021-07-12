package pool.handler

import com.raquo.laminar.api.L._

import org.scalajs.dom.console

import pool.{Context, Fault, State}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.util.{Failure, Success}

object StateHandler {
  def handle(context: Context,
             errors: EventBus[String],
             response: Future[Either[Fault, State]],
             handler: (Context, EventBus[String], State) => Unit): Unit = {
    response.onComplete {
      case Success(either) => either match {
        case Right(state) =>
          console.info(s"State: $state")
          handler(context, errors, state)
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