package poolmate

import java.time.*

import org.scalatest.funsuite.AnyFunSuite
import org.scalatest.matchers.should.Matchers

import DateTime.*

class DateTimeTest extends AnyFunSuite with Matchers:
  test("low date") {
    val localDate = LocalDate.of(2001, 1, 1)
    localDateToInt(localDate) shouldEqual 20010101
    localDateToInt(2001, 1, 1) shouldEqual 20010101
    localDateToString(localDate) shouldEqual "2001-01-01"
    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 20010101
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "2001-01-01"
  }

  test("high date") {
    val localDate = LocalDate.of(2001, 12, 15)
    localDateToInt(localDate) shouldEqual 20011215
    localDateToInt(2001, 12, 15) shouldEqual 20011215
    localDateToString(localDate) shouldEqual "2001-12-15"
    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 20011215
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "2001-12-15"
  }

  test("low time") {
    val localTime = LocalTime.of(2, 2)
    localTimeToInt(localTime) shouldEqual 202
    localTimeToInt(2, 2) shouldEqual 202
    localTimeToString(localTime) shouldEqual "02:02"
    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 202
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "02:02"
  }

  test("high time") {
    val localTime = LocalTime.of(18, 14)
    localTimeToInt(localTime) shouldEqual 1814
    localTimeToInt(18, 14) shouldEqual 1814
    localTimeToString(localTime) shouldEqual "18:14"
    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 1814
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "18:14"
  }