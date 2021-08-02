package pool

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine

import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

object AccountCache {
  def apply(): AccountCache = new AccountCache()
}

class AccountCache() {
  import Validators._

  private val conf = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[Account]]

  private val cache: Cache[Account] = CaffeineCache[Account](conf)

  def put(account: Account): Unit = {
    cache.put(account.license)(account)
    ()
  }

  def remove(account: Account): Unit = {
    cache.remove(account.license)
    ()
  }

  def isAccountActivated(license: String): Boolean = {
    val account = cache.get(license)
    if (account.nonEmpty) account.get.isActivated
    else false
  }
}