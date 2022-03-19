package poolmate

import com.raquo.laminar.api.L.*

import org.scalajs.dom.console.log

import Components.*
import Validators.*

object HomeView extends View:
    def apply(accountVar: Var[Account]): HtmlElement =
      div(
        bar(
          btn("Account").amend {
            onClick --> { _ =>
              log("Home -> Account menu item onClick")
              route(AccountPage)
            }
          },
          rbtn("Pools").amend {
            disabled <-- accountVar.signal.map { account => account.isDeactivated }
            onClick --> { _ =>
              log("Home -> Pools menu item onClick")
              route(PoolsPage)
            }
          }          
        )
      )