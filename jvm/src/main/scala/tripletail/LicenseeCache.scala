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
  import Validators._
  import store._

  private val conf = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[Licensee]]

  private val cache: Cache[Licensee] = CaffeineCache[Licensee](conf)

  def cacheLicensee(licensee: Licensee): Unit = {
    if (licensee.isActivated && cache.get(licensee.license).isEmpty) cache.put(licensee.license)(licensee)
    ()
  }

  def isLicenseActivated(license: String): Future[Boolean] = {
    if (cache.get(license).nonEmpty) Future.successful(true)
    else getLicensee(license).map {
      case Some(licensee) =>
        if (licensee.isActivated) {
          cache.put(licensee.license)(licensee)
          true
        } else false
      case None => false
    }
  }
}