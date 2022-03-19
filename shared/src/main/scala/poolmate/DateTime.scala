package poolmate

import java.time._

object DateTime:
  val dateFormatter = format.DateTimeFormatter.ofPattern("yyyy-MM-dd")
  val timeFormatter = format.DateTimeFormatter.ofPattern("HH:mm")

  def currentDate: Int = localDateToInt(LocalDate.now)

  def localDateToInt(localDate: LocalDate): Int =
    localDateToString(localDate).replace("-", "").toInt

  def localDateToInt(yyyy: Int, mm: Int, dd: Int): Int =
    localDateToInt(LocalDate.of(yyyy, mm, dd))

  def localDateToString(localDate: LocalDate): String =
    localDate.format(dateFormatter)

  def localDateAsStringToInt(localDate: String): Int =
    localDate.replace("-", "").toInt

  def localDateAsIntToString(localDate: Int): String =
    val localDateAsString = localDate.toString
    val yyyy = localDateAsString.substring(0, 4)
    val mm = localDateAsString.substring(4, 6)
    val dd = localDateAsString.substring(6, 8)
    LocalDate.of(yyyy.toInt, mm.toInt, dd.toInt).format(dateFormatter)

  def currentTime: Int = localTimeToInt(LocalTime.now)

  def localTimeToInt(localTime: LocalTime): Int =
    localTimeToString(localTime).replace(":", "").toInt

  def localTimeToInt(hh: Int, mm: Int): Int =
    localTimeToInt(LocalTime.of(hh, mm))

  def localTimeToString(localTime: LocalTime): String =
    localTime.format(timeFormatter)

  def localTimeAsStringToInt(localTime: String): Int =
    localTime.replace(":", "").toInt

  def localTimeAsIntToString(localTime: Int): String =
    val localTimeAsString = localTime.toString
    var hh = ""
    var mm = ""
    if localTimeAsString.length == 3 then
      hh = localTimeAsString.substring(0, 1)
      mm = localTimeAsString.substring(1, 3)
    else
      hh = localTimeAsString.substring(0, 2)
      mm = localTimeAsString.substring(2, 4)

    LocalTime.of(hh.toInt, mm.toInt).format(timeFormatter)

  def nano: Int = Instant.now().getNano