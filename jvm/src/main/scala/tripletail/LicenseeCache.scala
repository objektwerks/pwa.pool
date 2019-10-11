package tripletail

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.{ExecutionContext, Future}

object LicenseeCache {
  def apply(store: Store)(implicit ec: ExecutionContext): LicenseeCache = new LicenseeCache(store)
}

class LicenseeCache(store: Store)(implicit ec: ExecutionContext) {
  import store._

  private val conf = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[Licensee]]

  private val cache: Cache[Licensee] = CaffeineCache[Licensee](conf)

  private def isActive(licensee: Licensee): Boolean = licensee.license.nonEmpty && licensee.activated > 0 && licensee.deactivated == 0

  def cacheLicensee(licensee: Licensee): Unit = {
    if (cache.get(licensee.license).isEmpty) cache.put(licensee.license)(licensee)
    ()
  }

  def isLicenseActive(license: String): Future[Boolean] = {
    if (cache.get(license).nonEmpty) {
      Future.successful(isActive(cache.get(license).get))
    } else {
      getLicensee(license).flatMap { option =>
        if (option.nonEmpty) {
          val licensee = option.get
          cacheLicensee(licensee)
          Future.successful(isActive(cache.get(license).get))
        } else {
          Future.successful(false)
        }
      }
    }
  }
}