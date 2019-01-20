import java.util.concurrent.TimeUnit

import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.server.Route
import com.github.benmanes.caffeine.cache.Caffeine
import io.circe.generic.auto._
import io.circe.parser.decode
import scalacache.caffeine.CaffeineCache
import scalacache.modes.sync._
import scalacache.{Cache, Entry}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

package object tripletail {
  val cacheConf = Caffeine.newBuilder.maximumSize(10000L).expireAfterWrite(24, TimeUnit.HOURS).build[String, Entry[Licensee]]
  implicit val cache: Cache[Licensee] = CaffeineCache[Licensee](cacheConf)

  def isLicenseeSecure(license: String): Future[Boolean] = {
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

  def secure(route: Route): Route = headerValueByName("licensee") { json =>
    decode[Licensee](json) match {
      case Right(licensee) => onSuccess(isLicenseeSecure(licensee.license)) { isSecure =>
        if (isSecure) route else complete(StatusCodes.Unauthorized)
      }
      case Left(error) => extractLog { log =>
        log.error(error, s"Licensee json parsing failed: ${error.getMessage}")
        complete(StatusCodes.Unauthorized)
      }
    }
  }
}