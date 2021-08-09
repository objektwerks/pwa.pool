package pool

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class SerializerTest extends AnyFunSuite with Matchers {
  test("account") {
    import upickle.default._
    import Serializers._

    val account = Account("test@test.com")
    val accountJson = write[Account](account)
    println(account)
    println(accountJson)
    println(read[Account](accountJson))
    account shouldBe read[Account](accountJson)
  }

  test("pool") {
    import upickle.default._
    import Serializers._

    val pool = Pool(license = "abc123", name = "pool", built = 1991, lat = 26.85, lon = 82.29, volume = 10000)
    val poolJson = write[Pool](pool)
    println(pool)
    println(poolJson)
    println(read[Pool](poolJson))
    pool shouldBe read[Pool](poolJson)
  }

  test("jsoniter > pump") {
    import com.github.plokhotnyuk.jsoniter_scala.core._
    import Codecs.pumpCodec

    val pump = Pump(poolId = 1, installed = 1, model = "model.pump")
    val pumpByteArray = writeToArray(pump)
    pump shouldBe readFromArray(pumpByteArray)

    println(s"jsoniter pump: $pump")
    println(s"jsoniter pump as json: ${pumpByteArray.map(_.toChar).mkString}")
  }

  test("jsoniter > heater") {
    import com.github.plokhotnyuk.jsoniter_scala.core._
    import Codecs.heaterCodec

    val heater = Heater(poolId = 1, installed = 1, model = "model.heater")
    val heaterByteArray = writeToArray(heater)
    heater shouldBe readFromArray(heaterByteArray)

    println(s"jsoniter heater: $heater")
    println(s"jsoniter heater as json: ${heaterByteArray.map(_.toChar).mkString}")
  }
  
  test("jsoniter > timer") {
    import com.github.plokhotnyuk.jsoniter_scala.core._
    import Codecs.timerCodec

    val timer = Timer(poolId = 1, installed = 1, model = "model.timer")
    val timerByteArray = writeToArray(timer)
    timer shouldBe readFromArray(timerByteArray)

    println(s"jsoniter timer: $timer")
    println(s"jsoniter timer as json: ${timerByteArray.map(_.toChar).mkString}")
  }  
}