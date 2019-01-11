package tripletail

class PoolModelView(poolRestClient: PoolRestClient) {
  def init(): Unit = {
    println(poolRestClient)
    ()
  }
}

object PoolModelView {
  def apply(poolRestClient: PoolRestClient): PoolModelView = new PoolModelView(poolRestClient)
}