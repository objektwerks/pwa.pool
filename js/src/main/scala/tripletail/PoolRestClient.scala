package tripletail

class PoolRestClient(url: String) {
  println(url)
}

object PoolRestClient {
  def apply(url: String): PoolRestClient = new PoolRestClient(url)
}