package tripletail

import java.util.UUID

sealed trait Entity extends Product with Serializable

final case class Licensee(license: String = Licensee.generateLicense,
                          emailAddress: String,
                          created: Int = DateTime.currentDate,
                          activated: Int = 0,
                          deactivated: Int = 0) extends Entity {
  def toLicense: License = License(license)
}

object Licensee {
  val headerLicenseKey = "license"
  def generateLicense: String = UUID.randomUUID.toString.toLowerCase
}

final case class License(key: String) extends Entity

final case class Pool(id: Int = 0,
                      license: String,
                      built: Int,
                      lat: Double,
                      lon: Double,
                      volume: Int) extends Entity

final case class PoolId(id: Int = 0) extends Entity

final case class Surface(id: Int = 0,
                         poolId: Int,
                         installed: Int,
                         kind: String) extends Entity

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