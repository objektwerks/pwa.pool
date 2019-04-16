package tripletail

class PoolModelView(poolRestClient: PoolServerClient) {
  def init(): Unit = {
    println(poolRestClient)
    ()
  }
}

object PoolModelView {
  def apply(poolRestClient: PoolServerClient): PoolModelView = new PoolModelView(poolRestClient)
}