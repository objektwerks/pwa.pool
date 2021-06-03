package pool

object Urls {
    def apply(publicUrl: String, apiUrl: String): Urls = Urls(
      signup = s"$publicUrl/signup",
      signin = s"$publicUrl/signin",
      deactivate = s"$publicUrl/deactivatelicensee",
      reactivate = s"$publicUrl/reactivatelicensee",
      pools = s"$apiUrl/pools",
      poolsAdd = s"$apiUrl/pools/add",
      poolsUpdate = s"$apiUrl/pools/update"
    )
}

case class Urls(signup: String,
                signin: String, 
                deactivate: String, 
                reactivate: String, 
                pools: String,
                poolsAdd: String,
                poolsUpdate: String) extends Product with Serializable