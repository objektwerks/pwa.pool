package tripletail

import java.time.{LocalDate, LocalTime}

import org.scalatest.{FunSuite, Matchers}
import tripletail.DateTime._

class DateTimeTest extends FunSuite with Matchers {
  test("date") {
    val localDate = LocalDate.of(1999, 3, 13)

    localDateToInt(localDate) shouldEqual 19990313
    localDateToInt(1999, 3, 13) shouldEqual 19990313

    localDateToString(localDate) shouldEqual "1999-03-13"

    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 19990313
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "1999-03-13"
  }

  test("time") {
    val localTime = LocalTime.of(3, 33)

    localTimeToInt(localTime) shouldEqual 333
    localTimeToInt(3, 33) shouldEqual 333

    localTimeToString(localTime) shouldEqual "03:33"

    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 333
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "03:33"
  }
}