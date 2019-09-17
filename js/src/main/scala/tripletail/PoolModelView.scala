package tripletail

object PoolModelView {
  def apply(poolRestClient: PoolServerClient): PoolModelView = new PoolModelView(poolRestClient)
}

class PoolModelView(poolRestClient: PoolServerClient) {
  def init(): Unit = {
    println(poolRestClient)
    ()
  }
}