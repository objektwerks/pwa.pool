package tripletail

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object LicenseeCache {
  private val conf = Caffeine.newBuilder.maximumSize(1000L).expireAfterWrite(24, TimeUnit.HOURS).build[String, Entry[Licensee]]
  private val cache: Cache[Licensee] = CaffeineCache[Licensee](conf)

  def put(licensee: Licensee): String = {
    val key = licensee.license
    cache.put(key)(licensee)
    key
  }

  def isLicenseValid(license: String): Future[Boolean] = {
    if (cache.get(license).nonEmpty) {
      Future.successful(true)
    } else {
      PoolStore.getLicensee(license).flatMap { option =>
        if (option.nonEmpty) {
          val licensee = option.get
          cache.put(licensee.license)(licensee)
          Future.successful(true)
        } else {
          Future.successful(false)
        }
      }
    }
  }
}