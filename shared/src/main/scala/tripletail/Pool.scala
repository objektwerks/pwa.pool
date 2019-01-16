package tripletail

import java.time.{LocalDate, LocalTime}
import java.util.UUID

final case class Licensee(license: String = UUID.randomUUID.toString.toLowerCase, email: String,
                          activated: LocalDate = LocalDate.now, deactivated: Option[LocalDate] = None)

final case class Pool(id: Int, license: String, built: LocalDate, address: String, volume: Int)

final case class Surface(id: Int, poolId: Int, installed: LocalDate, kind: String)

final case class Pump(id: Int, poolId: Int, installed: LocalDate, model: String)

final case class Timer(id: Int, poolId: Int, installed: LocalDate, model: String)

final case class TimerSetting(id: Int, timerId: Int, set: LocalDate, setOn: LocalTime, setOff: LocalTime)

final case class Heater(id: Int, poolId: Int, installed: LocalDate, model: String)

final case class HeaterOn(id: Int, heaterId: Int, temp: Int, set: LocalDate)

final case class HeaterOff(id: Int, heaterId: Int, set: LocalDate)

final case class Cleaning(id: Int, poolId: Int, brush: Boolean, net: Boolean, vacuum: Boolean, skimmerBasket: Boolean,
                          pumpBasket: Boolean, pumpFilter: Boolean, pumpChlorineTablets: Int, deck: Boolean)

final case class Measurement(id: Int, poolId: Int, temp: Int, totalHardness: Int, totalChlorine: Int,
                             totalBromine: Int, freeChlorine: Int, ph: Double, totalAlkalinity: Int, cyanuricAcid: Int)

final case class Chemical(id: Int, poolId: Int, added: LocalDate, chemical: String, amount: Double, unit: String)

final case class Supply(id: Int, poolId: Int, purchased: LocalDate, cost: Double, item: String, amount: Double, unit: String)

final case class Repair(id: Int, poolId: Int, repaired: LocalDate, cost: Double, repair: String)