package tripletail

final case class Fault(cause: String,
                       code: Int = 500,
                       dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       nanoOf: Long = System.nanoTime) extends Product with Serializable