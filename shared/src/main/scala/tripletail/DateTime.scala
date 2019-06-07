package tripletail

import java.time.{LocalDate, LocalTime}
import java.time.format.DateTimeFormatter

object DateTime {
  val localDate = DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val localTime = DateTimeFormatter.ofPattern("HH:mm")

  def currentDate: String = LocalDate.now.format(localDate)

  def currentTime: String = LocalTime.now.format(localTime)
}