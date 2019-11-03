package tripletail

object IndexedStore {
  def apply(): IndexedStore = new IndexedStore()
}

class IndexedStore() {
  def getLicensee: Licensee = Licensee(emailAddress = "tripletail@gmail.com")
}