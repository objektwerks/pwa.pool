package tripletail

class RestClient(url: String) {
  println(url)
}

object RestClient {
  def apply(url: String): RestClient = new RestClient(url)
}