package pool

import org.scalajs.dom._
import org.scalajs.dom.ServiceWorkerGlobalScope.self
import org.scalajs.dom.window.navigator

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import scala.scalajs.js
import scala.scalajs.js.JSConverters._
import scala.util.{Failure, Success}

object ServiceWorker {
  val poolCache = "pool-cache"
  val poolAssets: js.Array[RequestInfo] = List[RequestInfo](
    "index.html",
    "favicon.ico",
    "w3c.css",
    "common.css",
    "logo.png",
    "logo-192.png",
    "logo-512.png"
  ).toJSArray

  def register(): Unit =
    navigator
      .serviceWorker
      .register("js/main.js")
      .toFuture
      .onComplete {
        case Success(registration) =>
          Context.log("[register] registered service worker")
          registration.update()
          registerEventListeners()
        case Failure(error) =>
          Context.log(s"[register] service worker registration failed: ${error.printStackTrace()}")
      }

  def registerEventListeners(): Unit = {
    Context.log(s"[registerEventListeners] registering event listeners...")

    self.addEventListener("install", (event: ExtendableEvent) => {
      Context.log(s"[install] service worker installed: ${event.toString}")
      event.waitUntil(toCache.toJSPromise)
    })

    self.addEventListener("activate", (event: ExtendableEvent) => {
      Context.log(s"[activate] service worker activated: ${event.toString}")
      invalidateCache()
      self.clients.claim()
    })

    self.addEventListener("fetch", (event: FetchEvent) => {
      if (event.request.cache == RequestCache.`only-if-cached`
        && event.request.mode != RequestMode.`same-origin`) {
        Context.log(s"[fetch] Bug [823392] cache === only-if-cached && mode !== same-orgin' > ${event.request.url}")
      } else {
        fromCache(event.request).onComplete {
          case Success(response) =>
            Context.log(s"[fetch] in cache > ${event.request.url}")
            response
          case Failure(error) =>
            Context.log(s"[fetch] not in cache, calling server: ${event.request.url} > ${error.printStackTrace()}")
            fetch(event.request)
              .toFuture
              .onComplete {
                case Success(response) => response
                case Failure(finalError) => Context.log(s"[fetch] final fetch failed: ${finalError.printStackTrace()}")
              }
        }
      }
    })

    Context.log(s"[registerEventListeners] event listeners registered.")
  }

  def toCache: Future[Unit] = {
    self.caches.get.open(poolCache)
      .toFuture
      .onComplete {
        case Success(cache) =>
          Context.log("[toCache] caching assets:")
          cache.addAll(poolAssets).toFuture
        case Failure(error) =>
          Context.log(s"[toCache] failed to cache assets: ${error.printStackTrace()}")
      }
    Future.unit
  }

  def fromCache(request: Request): Future[Response] = {
    self.caches.get.`match`(request)
      .toFuture
      .asInstanceOf[Future[Response]]
      .map { response: Response =>
        Context.log(s"[fromCache] matched cache request: ${request.url}")
        response
      }
  }

  def invalidateCache(): Unit = {
    self.caches.get.delete(poolCache)
      .toFuture
      .map { invalidatedCache =>
        if (invalidatedCache) {
          Context.log(s"[invalidateCache] cache invalidated: $invalidatedCache")
          toCache
        }
      }
    ()
  }
}