package tripletail

object Canvas {
  def apply(serverProxy: ServerProxy): Canvas = new Canvas(serverProxy)
}

class Canvas(serverProxy: ServerProxy) {
  def init(): Unit = {
    println(serverProxy)
    ()
  }
}