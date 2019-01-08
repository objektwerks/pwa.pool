package tripletail

import java.time.{Instant, LocalDate}

object Entity {
  final case class Owner(license: String, email: String)

  object Owner {
    def apply(email: String): Owner = Owner(license = s"$email-${Instant.now.toString}", email)
  }

  final case class Pool(id: Integer, license: String, built: LocalDate, lat: Float, lon: Float, volume: Integer)

  final case class Surface(id: Integer, pid: Integer, installed: LocalDate, kind: String)

  final case class Pump(id: Integer, pid: Integer, installed: LocalDate, model: String)
}