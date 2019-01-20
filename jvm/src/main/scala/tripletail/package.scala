import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object tripletail {
  val licenseeCacheConf = Caffeine.newBuilder.maximumSize(1000L).expireAfterWrite(24, TimeUnit.HOURS).build[String, Entry[Licensee]]
  implicit val licenseeCache: Cache[Licensee] = CaffeineCache[Licensee](licenseeCacheConf)

  def isLicenseValid(license: String): Future[Boolean] = {
    if (licenseeCache.get(license).nonEmpty) {
      Future.successful(true)
    } else {
      PoolStore.getLicensee(license).flatMap { option =>
        if (option.nonEmpty) {
          val licensee = option.get
          licenseeCache.put(licensee.license)(licensee)
          Future.successful(true)
        } else {
          Future.successful(false)
        }
      }
    }
  }

  def secure(route: Route): Route = headerValueByName("license") { license =>
    onSuccess(isLicenseValid(license)) { isValid =>
      if (isValid) route else complete(StatusCodes.Unauthorized)
    }
  }
}