package tripletail

import org.scalajs.dom._

object LicenseeStore {
  private val dbName = "licensee"
  private val dbVersion = 1
  private var licenseeCache: Option[Licensee] = None

  val openRequest = window.indexedDB.open(dbName, dbVersion)

  def getLicensee: Licensee = licenseeCache.getOrElse(putLicensee(Licensee(emailAddress = "tripletail@gmail.com")))

  def putLicensee(licensee: Licensee): Licensee = {
    licenseeCache = Some(licensee)
    licenseeCache.get
  }
}