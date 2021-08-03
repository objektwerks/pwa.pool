package pool.handler

import com.raquo.laminar.api.L._

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
          context.log(s"State: $state")
          handler(context, errors, state)
        case Left(fault) =>
          context.log(s"Fault: $fault")
          errors.emit(s"Fault: ${fault.cause}")
      }
      case Failure(failure) =>
        context.log(s"Failure: $failure")
        errors.emit(s"Failure: ${failure.getMessage}")
    }
  }
}