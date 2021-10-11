package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  import upickle.default._
  import Serializers._

  test("account") {
    val account = Account("test@test.com")
    val accountJson = write[Account](account)
    account shouldBe read[Account](accountJson)

    println(s"account: $account")
    println(s"write account json: $accountJson")
    println(s"read account json: ${read[Account](accountJson)}")
  }

  test("pool") {
    val pool = Pool(license = "abc123", name = "pool", built = 1991, volume = 10000)
    val poolJson = write[Pool](pool)
    pool shouldBe read[Pool](poolJson)
    
    println(s"pool: $pool")
    println(s"write pool json: $poolJson")
    println(s"read pool json: ${read[Pool](poolJson)}")

    val emptyPool = Pool()
    val emptyPoolJson = write[Pool](emptyPool)
    emptyPool shouldBe read[Pool](emptyPoolJson)

    println(s"empty pool: $emptyPool")
    println(s"write empty pool json: $emptyPoolJson")
    println(s"read empty pool json: ${read[Pool](emptyPoolJson)}")
  }
}