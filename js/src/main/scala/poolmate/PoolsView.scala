package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*

object PoolsView extends View:
  def apply(model: Model[Pool], accountVar: Var[Account]): HtmlElement =
    def handler(event: Either[Fault, Event]): Unit =
      event match
        case Right(event) =>
          event match
            case PoolsListed(pools: Seq[Pool]) =>
              clearErrors()
              model.setEntities(pools)
            case _ =>
        case Left(fault) => errorBus.emit(s"List pools failed: ${fault.cause}")

    div(
      bar(
        btn("Home").amend {
          onClick --> { _ =>
            log("Pools -> Home menu item onClick")
            route(HomePage)
          }
        }      
      ),
      div(
        onLoad --> { _ => 
          val command = ListPools(accountVar.now().license)
          call(command, handler)
        },
        hdr("Pools"),
        list(
          split(model.entitiesVar, (id: Long) => PoolPage(id))
        )
      ),
      cbar(
        btn("New").amend {
          onClick --> { _ =>
            log(s"Pools -> New onClick")
            route(PoolPage())
          }
        },        
        btn("Refresh").amend {
          onClick --> { _ =>
            log(s"Pools -> Refresh onClick")
            val command = ListPools(accountVar.now().license)
            call(command, handler)
          }
        }
      )
    )