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
  val cacheConf = Caffeine.newBuilder.maximumSize(10000L).expireAfterWrite(24, TimeUnit.HOURS).build[String, Entry[String]]
  implicit val cache: Cache[String] = CaffeineCache[String](cacheConf)

  def isLicenseValid(license: String): Future[Boolean] = {
    if (cache.get(license).nonEmpty) {
      Future.successful(true)
    } else {
      PoolStore.getLicensee(license).flatMap { option =>
        if (option.nonEmpty) {
          val _license = option.get.license
          cache.put(_license)(_license)
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