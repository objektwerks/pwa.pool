package pool

import com.raquo.laminar.api.L
import com.raquo.laminar.api.L.*
import com.raquo.waypoint.*

import upickle.default.*

import Serializers.given

object PageRouter:
  given poolPageRW: ReadWriter[PoolPage] = macroRW
  given entityPageRW: ReadWriter[EntityPage] = macroRW
  given pageRW: ReadWriter[Page] = macroRW
  
  val poolRoute = Route[PoolPage, Long](
    encode = poolPage => poolPage.id,
    decode = arg => PoolPage(id = arg),
    pattern = root / "home" / "pools" / segment[Long] / endOfSegments
  )

  val routes = List(
    Route.static(RootPage, root / endOfSegments),
    Route.static(RegisterPage, root / "register" / endOfSegments),
    Route.static(LoginPage, root / "login" / endOfSegments),
    Route.static(HomePage, root / "home" / endOfSegments),
    Route.static(AccountPage, root / "home" / "account" / endOfSegments),
    Route.static(PoolsPage, root / "home" / "pools" / endOfSegments),
    poolRoute
  )

  val router = new com.raquo.waypoint.Router[Page](
    routes = routes,
    serializePage = page => write(page)(pageRW),
    deserializePage = pageAsString => read(pageAsString)(pageRW),
    getPageTitle = _.title,
  )(
    $popStateEvent = L.windowEvents.onPopState,
    owner = L.unsafeWindowOwner
  )

  val splitter = SplitRender[Page, HtmlElement](router.$currentPage)
    .collectStatic(IndexPage) { IndexView() }
    .collectStatic(RegisterPage) { RegisterView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(LoginPage) { LoginView(Model.emailAddressVar, Model.pinVar, Model.accountVar) }
    .collectStatic(HomePage) { HomeView(Model.accountVar) }
    .collectStatic(AccountPage) { AccountView(Model.accountVar) }
    .collectStatic(PoolsPage) { PoolsView(Model.pools, Model.accountVar) }
    .collect[PoolPage] { page => PoolView(Model.pools.setSelectedEntityById(page.id), Model.accountVar) }