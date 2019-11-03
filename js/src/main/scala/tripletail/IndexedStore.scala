package tripletail

object IndexedStore {
  private var licenseeCache: Option[Licensee] = None

  def getLicensee: Licensee = licenseeCache.getOrElse(putLicensee(Licensee(emailAddress = "tripletail@gmail.com")))

  def putLicensee(licensee: Licensee): Licensee = {
    licenseeCache = Some(licensee)
    licenseeCache.get
  }
}