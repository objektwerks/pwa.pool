package pool.handler

import com.raquo.laminar.api.L._

import pool.{Context, Event, Fault}

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
          println(s"Event: $event")
          handler(context, errors, event)
        case Left(fault) =>
          println(s"Fault: $fault")
          errors.emit(s"Fault: $fault")
      }
      case Failure(failure) =>
        println(s"Failure: $failure")
        errors.emit(s"Failure: $failure")
    }
  }
}