package pool

case class Urls(publicUrl: String, apiUrl: String) extends Product with Serializable {
  val signup = s"$publicUrl/signup"
  val signin = s"$publicUrl/signin"
  val deactivate = s"$publicUrl/deactivatelicensee"
  val reactivate = s"$publicUrl/reactivatelicensee"
  val pools = s"$apiUrl/pools"
  val poolsAdd = s"$apiUrl/pools/add"
  val poolsUpdate = s"$apiUrl/pools/update"
}