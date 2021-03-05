package pool

object Canvas {
  def apply(serverProxy: ServerProxy,
            serverUrl: String,
            apiUrl: String): Canvas = new Canvas(serverProxy, serverUrl, apiUrl)
}

class Canvas(serverProxy: ServerProxy, serverUrl: String, apiUrl: String) {
  def init(): Unit = {
    println(serverProxy)
    println(serverUrl)
    println(apiUrl)
    ()
  }
}