package pool

import com.github.plokhotnyuk.jsoniter_scala.core._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CodecsTest extends AnyFunSuite with Matchers {
  import Codecs._

  test("pump") {
    val pump = Pump(poolId = 1, installed = 1, model = "model.pump")
    val pumpByteArray = writeToArray[Pump](pump)
    pump shouldBe readFromArray[Pump](pumpByteArray)
    pump.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter pump: $pump")
    println(s"jsoniter pump as json: ${pumpByteArray.map(_.toChar).mkString}")
  }

  test("heater") {
    val heater = Heater(poolId = 1, installed = 1, model = "model.heater")
    val heaterByteArray = writeToArray[Heater](heater)
    heater shouldBe readFromArray[Heater](heaterByteArray)
    heater.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter heater: $heater")
    println(s"jsoniter heater as json: ${heaterByteArray.map(_.toChar).mkString}")
  }
  
  test("timer") {
    val timer = Timer(poolId = 1, installed = 1, model = "model.timer")
    val timerByteArray = writeToArray[Timer](timer)
    timer shouldBe readFromArray[Timer](timerByteArray)
    timer.isInstanceOf[Entity] shouldBe true

    println(s"jsoniter timer: $timer")
    println(s"jsoniter timer as json: ${timerByteArray.map(_.toChar).mkString}")
  }  
}