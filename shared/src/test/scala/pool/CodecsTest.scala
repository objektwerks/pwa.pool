package pool

import com.github.plokhotnyuk.jsoniter_scala.core._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

class CodecsTest extends AnyFunSuite with Matchers {
  import Codecs._
  import Codecs.EntityCodecs._

  test("pump") {
    val pump = Pump(poolId = 1, installed = 1, model = "model.pump")
    val pumpJson = writeToString[Pump](pump)
    val entityJson = writeToString[Entity](pump)

    pump shouldBe readFromString[Pump](pumpJson)
    pump shouldBe readFromString[Entity](entityJson)

    println(s"jsoniter pump: $pump")
    println(s"jsoniter pump as json: $pumpJson")
    println(s"jsoniter pump as entity json: $entityJson")
  }

  test("heater") {
    val heater = Heater(poolId = 1, installed = 1, model = "model.heater")
    val heaterJson = writeToString[Heater](heater)
    val entityJson = writeToString[Entity](heater)

    heater shouldBe readFromString[Heater](heaterJson)
    heater shouldBe readFromString[Entity](entityJson)

    println(s"jsoniter heater: $heater")
    println(s"jsoniter heater as json: $heaterJson")
    println(s"jsoniter heater as entity json: $entityJson")
  }

  test("timer") {
    val timer = Timer(poolId = 1, installed = 1, model = "model.timer")
    val timerJson = writeToString[Timer](timer)
    val entityJson = writeToString[Entity](timer)

    timer shouldBe readFromString[Timer](timerJson)
    timer shouldBe readFromString[Entity](entityJson)

    println(s"jsoniter timer: $timer")
    println(s"jsoniter timer as json: $timerJson")
    println(s"jsoniter timer as entity json: $entityJson")
  }  
}