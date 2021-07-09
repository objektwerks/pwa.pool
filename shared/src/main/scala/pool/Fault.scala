package pool

final case class Fault(dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       nanoOf: Int = DateTime.nano,
                       code: Int = 500,
                       cause: String) extends Product with Serializable