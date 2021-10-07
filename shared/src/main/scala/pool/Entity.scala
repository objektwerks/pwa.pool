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
                         pin: Int,
                         activated: Int,
                         deactivated: Int) extends Entity {
  def toLicense: License = License(license)
}

object Account {
  def apply(email: String): Account = Account(
    license = UUID.randomUUID.toString.toLowerCase,
    email = email,
    pin = Math.abs(Random.nextInt()),
    activated = DateTime.currentDate,
    deactivated = 0
  )
  val emptyAccount = Account(
    license = "",
    email = "",
    pin = 0,
    activated = 0,
    deactivated = 0
  )
  val emptyLicense = ""
  val licenseHeader = "License"
}

final case class License(key: String) extends Entity

final case class Pool(id: Int = 0,
                      license: String = "",
                      name: String = "",
                      built: Int = 0,
                      lat: Double = 0.0,
                      lon: Double = 0.0,
                      volume: Int = 0) extends Entity

final case class PoolId(id: Int) extends Entity

final case class Surface(id: Int = 0,
                         poolId: Int = 0,
                         installed: Int = 0,
                         kind: String = "") extends Entity

final case class Pump(id: Int = 0,
                      poolId: Int,
                      installed: Int,
                      model: String) extends Entity

final case class Timer(id: Int = 0,
                       poolId: Int,
                       installed: Int,
                       model: String) extends Entity

final case class TimerId(id: Int = 0) extends Entity

final case class TimerSetting(id: Int = 0,
                              timerId: Int,
                              created: Int,
                              timeOn: Int,
                              timeOff: Int) extends Entity

final case class Heater(id: Int = 0,
                        poolId: Int,
                        installed: Int,
                        model: String) extends Entity

final case class HeaterId(id: Int = 0) extends Entity

final case class HeaterSetting(id: Int = 0,
                               heaterId: Int,
                               temp: Int,
                               dateOn: Int,
                               dateOff: Int = 0) extends Entity

final case class Measurement(id: Int = 0,
                             poolId: Int,
                             measured: Int,
                             temp: Int = 85,
                             totalHardness: Int = 375,
                             totalChlorine: Int = 3,
                             totalBromine: Int = 5,
                             freeChlorine: Int = 3,
                             ph: Double = 7.4,
                             totalAlkalinity: Int = 100,
                             cyanuricAcid: Int = 50) extends Entity

final case class Cleaning(id: Int = 0,
                          poolId: Int,
                          cleaned: Int,
                          brush: Boolean = true,
                          net: Boolean = true,
                          vacuum: Boolean = false,
                          skimmerBasket: Boolean = true,
                          pumpBasket: Boolean = false,
                          pumpFilter: Boolean = false,
                          deck: Boolean = false) extends Entity

final case class Chemical(id: Int = 0,
                          poolId: Int,
                          added: Int,
                          chemical: String,
                          amount: Double,
                          unit: String) extends Entity

final case class Supply(id: Int = 0,
                        poolId: Int,
                        purchased: Int,
                        cost: Double,
                        item: String,
                        amount: Double,
                        unit: String) extends Entity

final case class Repair(id: Int = 0,
                        poolId: Int,
                        repaired: Int,
                        cost: Double,
                        repair: String) extends Entity