package pool

import java.util.UUID

import scala.util.Random

sealed trait Entity extends Product with Serializable

final case class Email(id : String,
                       license: String,
                       address: String,
                       processed: Boolean = false,
                       valid: Boolean = false) extends Entity

final case class Account(license: String,
                         email: String,
                         pin: String,
                         activated: Int,
                         deactivated: Int) extends Entity {
  def deriveLicense: License = License(license)
}

object Account {
  private val specialChars = "~!@#$%^&*{}-+<>?/:;".toList
  private val random = new Random

  private def newLicense: String = UUID.randomUUID.toString
  private def newSpecialChar: Char = specialChars(random.nextInt(specialChars.length))
  private def newPin: String = Random.shuffle(
    Random
      .alphanumeric
      .take(7)
      .mkString
      .prepended(newSpecialChar)
      .appended(newSpecialChar)
  ).mkString

  val emptyAccount = Account(
    license = "",
    email = "",
    pin = "",
    activated = 0,
    deactivated = 0
  )
  val emptyLicense = ""
  val licenseHeader = "License"

  def apply(email: String): Account = Account(
    license = newLicense,
    email = email,
    pin = newPin,
    activated = DateTime.currentDate,
    deactivated = 0
  )
}

final case class License(key: String) extends Entity

final case class Pool(id: Int = 0,
                      license: String = "",
                      name: String = "",
                      built: Int = 0,
                      volume: Int = 1000) extends Entity

final case class PoolId(id: Int = 0) extends Entity

final case class Surface(id: Int = 0,
                         poolId: Int = 0,
                         installed: Int = 0,
                         kind: String = "") extends Entity

final case class Pump(id: Int = 0,
                      poolId: Int = 0,
                      installed: Int = 0,
                      model: String = "") extends Entity

final case class Timer(id: Int = 0,
                       poolId: Int = 0,
                       installed: Int = 0,
                       model: String = "") extends Entity

final case class TimerId(id: Int = 0) extends Entity

final case class TimerSetting(id: Int = 0,
                              timerId: Int = 0,
                              created: Int = 0,
                              timeOn: Int = 0,
                              timeOff: Int = 0) extends Entity

final case class Heater(id: Int = 0,
                        poolId: Int = 0,
                        installed: Int = 0,
                        model: String = "") extends Entity

final case class HeaterId(id: Int = 0) extends Entity

final case class HeaterSetting(id: Int = 0,
                               heaterId: Int = 0,
                               temp: Int = 0,
                               dateOn: Int = 0,
                               dateOff: Int = 0) extends Entity

final case class Measurement(id: Int = 0,
                             poolId: Int = 0,
                             measured: Int = 0,
                             temp: Int = 85,
                             totalHardness: Int = 375,
                             totalChlorine: Int = 3,
                             totalBromine: Int = 5,
                             freeChlorine: Int = 3,
                             ph: Double = 7.4,
                             totalAlkalinity: Int = 100,
                             cyanuricAcid: Int = 50) extends Entity

final case class Cleaning(id: Int = 0,
                          poolId: Int = 0,
                          cleaned: Int = 0,
                          brush: Boolean = true,
                          net: Boolean = true,
                          vacuum: Boolean = false,
                          skimmerBasket: Boolean = true,
                          pumpBasket: Boolean = false,
                          pumpFilter: Boolean = false,
                          deck: Boolean = false) extends Entity

final case class Chemical(id: Int = 0,
                          poolId: Int = 0,
                          added: Int = 0,
                          chemical: String = "",
                          amount: Double = 0.0,
                          unit: String = "") extends Entity

final case class Supply(id: Int = 0,
                        poolId: Int = 0,
                        purchased: Int = 0,
                        item: String = "",
                        amount: Double = 0.0,
                        unit: String = "",
                        cost: Double = 0.0) extends Entity

final case class Repair(id: Int = 0,
                        poolId: Int = 0,
                        repaired: Int = 0,
                        repair: String = "",
                        cost: Double = 0.0) extends Entity