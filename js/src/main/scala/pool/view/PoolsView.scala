package pool.view

import com.raquo.laminar.api.L._
import pool.{Context, License, Pools, ServerProxy}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

object PoolsView {
  val id = getClass.getSimpleName
  val errors = new EventBus[String]

  def pools(context: Context): Unit = {
    val license = License(context.account.now().license)
    println(s"Entity: $license")
    ServerProxy.post(context.poolsUrl, license.key, license).onComplete {
      case Success(either) => either match {
        case Right(state) => state match {
          case pools: Pools =>
            println(s"Success: $state")
            context.pools.set(pools)
          case _ => errors.emit(s"Invalid: $state")
        }
        case Left(fault) =>
          println(s"Fault: $fault")
          errors.emit(s"Fault: $fault")
      }
      case Failure(failure) =>
        println(s"Failure: $failure")
        errors.emit(s"Failure: $failure")
    }
  }

  def apply(context: Context): Div = {
    println(context)
    div(idAttr(id), cls("w3-container"), display("none"),
      h6(cls("w3-indigo"), "Pools"),
      div(cls("w3-panel w3-red"),
        child.text <-- errors.events
      ),
      ul(idAttr("pools"), cls("w3-ul w3-hoverable")),
      div(cls("w3-bar"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Add"),
        button(cls("w3-bar-item w3-button w3-margin w3-text-indigo"), "Edit")
      )
    )
  }
}