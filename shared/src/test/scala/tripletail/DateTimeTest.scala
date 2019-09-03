package tripletail

import java.time.{LocalDate, LocalTime}

import org.scalatest.{FunSuite, Matchers}
import tripletail.DateTime._

class DateTimeTest extends FunSuite with Matchers {
  test("date") {
    val localDate = LocalDate.of(1999, 3, 3)

    localDateToInt(localDate) shouldEqual 19990303
    localDateToInt(1999, 3, 3) shouldEqual 19990303

    localDateToString(localDate) shouldEqual "1999-03-03"

    localDateAsStringToInt(localDateToString(localDate)) shouldEqual 19990303
    localDateAsIntToString(localDateToInt(localDate)) shouldEqual "1999-03-03"
  }

  test("time") {
    val localTime = LocalTime.of(3, 3)

    localTimeToInt(localTime) shouldEqual 303
    localTimeToInt(3, 3) shouldEqual 303

    localTimeToString(localTime) shouldEqual "03:03"

    localTimeAsStringToInt(localTimeToString(localTime)) shouldEqual 303
    localTimeAsIntToString(localTimeToInt(localTime)) shouldEqual "03:03"
  }
}