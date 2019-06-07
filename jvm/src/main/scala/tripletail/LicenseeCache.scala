package tripletail

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.{ExecutionContext, Future}

object LicenseeCache {
  def apply(poolStore: PoolStore)(implicit ec: ExecutionContext): LicenseeCache = new LicenseeCache(poolStore)
}

class LicenseeCache(poolStore: PoolStore)(implicit ec: ExecutionContext) {
  import poolStore._

  private val conf = Caffeine.newBuilder.maximumSize(1000L).expireAfterWrite(24, TimeUnit.HOURS).build[String, Entry[Licensee]]
  private val cache: Cache[Licensee] = CaffeineCache[Licensee](conf)

  def cacheLicensee(licensee: Licensee): Unit = {
    if (cache.get(licensee.license).isEmpty) cache.put(licensee.license)(licensee)
    ()
  }

  def isLicenseValid(license: String): Future[Boolean] = {
    if (cache.get(license).nonEmpty) {
      Future.successful(true)
    } else {
      getLicensee(license).flatMap { option =>
        if (option.nonEmpty) {
          val licensee = option.get
          cacheLicensee(licensee)
          Future.successful(true)
        } else {
          Future.successful(false)
        }
      }
    }
  }
}