package pool

import com.github.plokhotnyuk.jsoniter_scala.core._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CodecsTest extends AnyFunSuite with Matchers {
  test("pump") {
    import Codecs.pumpCodec

    val pump = Pump(poolId = 1, installed = 1, model = "model.pump")
    val pumpByteArray = writeToArray(pump)
    pump shouldBe readFromArray(pumpByteArray)
    pump.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter pump: $pump")
    println(s"jsoniter pump as json: ${pumpByteArray.map(_.toChar).mkString}")
  }

  test("heater") {
    import Codecs.heaterCodec

    val heater = Heater(poolId = 1, installed = 1, model = "model.heater")
    val heaterByteArray = writeToArray(heater)
    heater shouldBe readFromArray(heaterByteArray)
    heater.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter heater: $heater")
    println(s"jsoniter heater as json: ${heaterByteArray.map(_.toChar).mkString}")
  }
  
  test("timer") {
    import Codecs.timerCodec

    val timer = Timer(poolId = 1, installed = 1, model = "model.timer")
    val timerByteArray = writeToArray(timer)
    timer shouldBe readFromArray(timerByteArray)
    timer.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter timer: $timer")
    println(s"jsoniter timer as json: ${timerByteArray.map(_.toChar).mkString}")
  }  
}