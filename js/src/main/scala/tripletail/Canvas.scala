package tripletail

object Canvas {
  def apply(licenseeStore: LicenseeStore,
            serverProxy: ServerProxy,
            serverUrl: String,
            apiUrl: String): Canvas = new Canvas(licenseeStore, serverProxy, serverUrl, apiUrl)
}

class Canvas(licenseeStore: LicenseeStore, serverProxy: ServerProxy, serverUrl: String, apiUrl: String) {
  def init(): Unit = {
    println(licenseeStore)
    println(serverProxy)
    println(serverUrl)
    println(apiUrl)
    ()
  }
}