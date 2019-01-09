package tripletail

import java.sql.Timestamp
import java.time.{LocalDate, LocalDateTime}

object Entity {
  private final case class Owner(email: String, established: Timestamp, license: String)

  object Owner {
    def apply(email: String): Owner = {
      val established = Timestamp.valueOf(LocalDateTime.now)
      Owner(email, established, s"$email-${established.toLocalDateTime.toString}")
    }
  }

  final case class Pool(id: Integer, license: String, built: LocalDate, lat: Double, lon: Double, volume: Integer)

  final case class Surface(id: Integer, poolId: Integer, installed: LocalDate, kind: String)

  final case class Pump(id: Integer, poolId: Integer, installed: LocalDate, model: String)
}