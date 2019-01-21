package tripletail

import java.time.Instant
import java.util.UUID

final case class Licensee(license: String = UUID.randomUUID.toString.toLowerCase, email: String,
                          activated: Long = Instant.now.toEpochMilli, deactivated: Option[Long] = None)

final case class Pool(id: Int, license: String, built: Long, lat: Long, lon: Long, volume: Int)

final case class Surface(id: Int, poolId: Int, installed: Long, kind: String)

final case class Pump(id: Int, poolId: Int, installed: Long, model: String)

final case class Timer(id: Int, poolId: Int, installed: Long, model: String)

final case class TimerSetting(id: Int, timerId: Int, set: Long, setOn: Long, setOff: Long)

final case class Heater(id: Int, poolId: Int, installed: Long, model: String)

final case class HeaterOn(id: Int, heaterId: Int, temp: Int, set: Long)

final case class HeaterOff(id: Int, heaterId: Int, set: Long)

final case class Cleaning(id: Int, poolId: Int, brush: Boolean, net: Boolean, vacuum: Boolean, skimmerBasket: Boolean,
                          pumpBasket: Boolean, pumpFilter: Boolean, pumpChlorineTablets: Int, deck: Boolean)

final case class Measurement(id: Int, poolId: Int, temp: Int, totalHardness: Int, totalChlorine: Int,
                             totalBromine: Int, freeChlorine: Int, ph: Double, totalAlkalinity: Int, cyanuricAcid: Int)

final case class Chemical(id: Int, poolId: Int, added: Long, chemical: String, amount: Double, unit: String)

final case class Supply(id: Int, poolId: Int, purchased: Long, cost: Double, item: String, amount: Double, unit: String)

final case class Repair(id: Int, poolId: Int, repaired: Long, cost: Double, repair: String)