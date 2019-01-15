package tripletail

class ModelView(poolRestClient: RestClient) {
  def init(): Unit = {
    println(poolRestClient)
    ()
  }
}

object ModelView {
  def apply(poolRestClient: RestClient): ModelView = new ModelView(poolRestClient)
}