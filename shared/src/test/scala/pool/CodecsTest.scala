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

    println(s"pump: $pump")
    println(s"pump as json: ${pumpByteArray.map(_.toChar).mkString}")
  }

  test("heater") {
    import Codecs.heaterCodec

    val heater = Heater(poolId = 1, installed = 1, model = "model.heater")
    val heaterByteArray = writeToArray(heater)
    heater shouldBe readFromArray(heaterByteArray)

    println(s"heater: $heater")
    println(s"heater as json: ${heaterByteArray.map(_.toChar).mkString}")
  }
  
  test("timer") {
    import Codecs.timerCodec

    val timer = Timer(poolId = 1, installed = 1, model = "model.timer")
    val timerByteArray = writeToArray(timer)
    timer shouldBe readFromArray(timerByteArray)

    println(s"timer: $timer")
    println(s"timer as json: ${timerByteArray.map(_.toChar).mkString}")
  }  
}