package pool

object Canvas {
  def apply(serverProxy: ServerProxy, apiUrl: String): Canvas = new Canvas(serverProxy, apiUrl)
}

class Canvas(serverProxy: ServerProxy, apiUrl: String) {
  def init(): Unit = {
    println(serverProxy)
    println(apiUrl)
    ()
  }
}