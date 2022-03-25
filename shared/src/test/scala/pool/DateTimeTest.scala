package pool

import java.time._

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import pool.DateTime._

class DateTimeTest extends AnyFunSuite with Matchers {
  test("low date") {
    val localDate = LocalDate.of(1999, 3, 3)
    localDateToInt(localDate) shouldEqual 19990303
    localDateToInt(1999, 3, 3) shouldEqual 19990303
    localDateToString(localDate) shouldEqual "1999-03-03"
    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 19990303
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "1999-03-03"
  }

  test("high date") {
    val localDate = LocalDate.of(1999, 12, 13)
    localDateToInt(localDate) shouldEqual 19991213
    localDateToInt(1999, 12, 13) shouldEqual 19991213
    localDateToString(localDate) shouldEqual "1999-12-13"
    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 19991213
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "1999-12-13"
  }

  test("low time") {
    val localTime = LocalTime.of(3, 3)
    localTimeToInt(localTime) shouldEqual 303
    localTimeToInt(3, 3) shouldEqual 303
    localTimeToString(localTime) shouldEqual "03:03"
    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 303
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "03:03"
  }

  test("high time") {
    val localTime = LocalTime.of(23, 33)
    localTimeToInt(localTime) shouldEqual 2333
    localTimeToInt(23, 33) shouldEqual 2333
    localTimeToString(localTime) shouldEqual "23:33"
    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 2333
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "23:33"
  }
}