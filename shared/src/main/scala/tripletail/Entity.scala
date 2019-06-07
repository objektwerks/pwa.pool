package tripletail

import java.util.UUID

sealed trait Entity extends Product with Serializable

final case class Licensee(license: String = UUID.randomUUID.toString.toLowerCase,
                          email: String,
                          activated: String = DateTime.currentDate,
                          deactivated: Option[String] = None) extends Entity

object Licensee {
  val licenseHeaderKey = "license"
}

final case class Pool(id: Int = 0,
                      license: String,
                      built: String,
                      lat: Double,
                      lon: Double,
                      volume: Int) extends Entity

final case class PoolId(id: Int = 0) extends Entity

final case class Surface(id: Int = 0,
                         poolId: Int,
                         installed: String,
                         kind: String) extends Entity

final case class Pump(id: Int = 0,
                      poolId: Int,
                      installed: String,
                      model: String) extends Entity

final case class Timer(id: Int = 0,
                       poolId: Int,
                       installed: String,
                       model: String) extends Entity

final case class TimerId(id: Int = 0) extends Entity

final case class TimerSetting(id: Int = 0,
                              timerId: Int,
                              set: String,
                              setOn: String,
                              setOff: String) extends Entity

final case class Heater(id: Int = 0,
                        poolId: Int,
                        installed: String,
                        model: String) extends Entity

final case class HeaterId(id: Int = 0) extends Entity

final case class HeaterOn(id: Int = 0,
                          heaterId: Int,
                          temp: Int,
                          set: String) extends Entity

final case class HeaterOff(id: Int = 0,
                           heaterId: Int,
                           set: String) extends Entity

final case class Cleaning(id: Int = 0,
                          poolId: Int,
                          brush: Boolean,
                          net: Boolean,
                          vacuum: Boolean,
                          skimmerBasket: Boolean,
                          pumpBasket: Boolean,
                          pumpFilter: Boolean,
                          pumpChlorineTablets: Int,
                          deck: Boolean) extends Entity

final case class Measurement(id: Int = 0,
                             poolId: Int,
                             temp: Int,
                             totalHardness: Int,
                             totalChlorine: Int,
                             totalBromine: Int,
                             freeChlorine: Int,
                             ph: Double,
                             totalAlkalinity: Int,
                             cyanuricAcid: Int) extends Entity

final case class Chemical(id: Int = 0,
                          poolId: Int,
                          added: String,
                          chemical: String,
                          amount: Double,
                          unit: String) extends Entity

final case class Supply(id: Int = 0,
                        poolId: Int,
                        purchased: String,
                        cost: Double,
                        item: String,
                        amount: Double,
                        unit: String) extends Entity

final case class Repair(id: Int = 0,
                        poolId: Int,
                        repaired: String,
                        cost: Double,
                        repair: String) extends Entity