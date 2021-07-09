package pool

final case class Fault(id: Int = 0,
                       dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       code: Int = 500,
                       cause: String) extends Product with Serializable