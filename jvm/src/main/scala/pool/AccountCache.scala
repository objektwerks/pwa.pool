package pool

import com.github.benmanes.caffeine.cache.Caffeine
import com.typesafe.scalalogging.LazyLogging

import java.util.concurrent.TimeUnit

import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

object AccountCache {
  def apply(): AccountCache = new AccountCache()
}

class AccountCache extends LazyLogging {
  import Validators._

  private val cacheBuilder = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[Account]]

  private val cache: Cache[Account] = CaffeineCache[Account](cacheBuilder)

  def put(account: Account): Unit = {
    cache.put(account.license)(account)
    logger.info("*** Cache put account: {}", account)
    ()
  }

  def remove(account: Account): Unit = {
    cache.remove(account.license)
    logger.info("*** Cache remove account: {}", account)
    ()
  }

  def isAccountActivated(license: String): Boolean = {
    val account = cache.get(license)
    if (account.nonEmpty) {
      val isActivated = account.get.isActivated
      logger.info("*** Cache account is activated: {}", isActivated)
      isActivated
    }
    else {
      logger.info("*** Cache account is not cached!")
      false
    }
  }
}