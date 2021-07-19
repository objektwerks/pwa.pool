package pool

import java.util.concurrent.TimeUnit

import com.github.benmanes.caffeine.cache.Caffeine

import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.{ExecutionContext, Future}

object AccountCache {
  def apply(store: Store)(implicit ec: ExecutionContext): AccountCache = new AccountCache(store)
}

class AccountCache(store: Store)(implicit ec: ExecutionContext) {
  import Validators._

  private val conf = Caffeine
    .newBuilder
    .maximumSize(1000L)
    .expireAfterWrite(24, TimeUnit.HOURS)
    .build[String, Entry[Account]]

  private val cache: Cache[Account] = CaffeineCache[Account](conf)

  def cacheAccount(account: Account): Unit = {
    if (account.isActivated && cache.get(account.license).isEmpty)
      cache.put(account.license)(account)
    ()
  }

  def decacheAccount(account: Account): Unit = {
    if (account.isDeactivated && cache.get(account.license).nonEmpty)
      cache.remove(account.license)
    ()
  }

  def isAccountActived(license: String): Future[Boolean] =
    if (cache.get(license).nonEmpty)
      Future.successful(cache.get(license).get.isActivated)
    else store.getAccountByLicense(license).map {
      case Some(account) =>
        if (account.isActivated) {
          cache.put(account.license)(account)
          true
        } else false
      case None => false
    }
}