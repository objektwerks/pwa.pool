package tripletail

object IndexedStore {
  private var licenseeCache: Option[Licensee] = None

  def getLicensee: Licensee =
    if (licenseeCache.nonEmpty) licenseeCache.get
    else putLicensee(Licensee(emailAddress = "tripletail@gmail.com"))

  def putLicensee(licensee: Licensee): Licensee = {
    licenseeCache = Some(licensee)
    licenseeCache.get
  }
}