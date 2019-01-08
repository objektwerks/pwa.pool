package tripletail

import java.time.{Instant, LocalDate}

object Entity {
  def createLicense(email: String): String = s"$email-${Instant.now.toString}"

  final case class Owner(license: String, email: String)

  final case class Pool(id: Integer, license: String, built: LocalDate, lat: Float, lon: Float, volume: Integer)

  final case class Surface(id: Integer, pid: Integer, installed: LocalDate, kind: String)


}