package tripletail

import java.time.Instant
import java.util.UUID

sealed trait Entity extends Product with Serializable

final case class Licensee(license: String = UUID.randomUUID.toString.toLowerCase, email: String,
                          activated: Long = Instant.now.toEpochMilli, deactivated: Option[Long] = None) extends Entity

final case class Pool(id: Int, license: String, built: Long, lat: Long, lon: Long, volume: Int) extends Entity

final case class Surface(id: Int, poolId: Int, installed: Long, kind: String) extends Entity

final case class Pump(id: Int, poolId: Int, installed: Long, model: String) extends Entity

final case class Timer(id: Int, poolId: Int, installed: Long, model: String) extends Entity

final case class TimerSetting(id: Int, timerId: Int, set: Long, setOn: Long, setOff: Long) extends Entity

final case class Heater(id: Int, poolId: Int, installed: Long, model: String) extends Entity

final case class HeaterOn(id: Int, heaterId: Int, temp: Int, set: Long) extends Entity

final case class HeaterOff(id: Int, heaterId: Int, set: Long) extends Entity

final case class Cleaning(id: Int, poolId: Int, brush: Boolean, net: Boolean, vacuum: Boolean, skimmerBasket: Boolean,
                          pumpBasket: Boolean, pumpFilter: Boolean, pumpChlorineTablets: Int, deck: Boolean) extends Entity

final case class Measurement(id: Int, poolId: Int, temp: Int, totalHardness: Int, totalChlorine: Int,
                             totalBromine: Int, freeChlorine: Int, ph: Double, totalAlkalinity: Int, cyanuricAcid: Int) extends Entity

final case class Chemical(id: Int, poolId: Int, added: Long, chemical: String, amount: Double, unit: String) extends Entity

final case class Supply(id: Int, poolId: Int, purchased: Long, cost: Double, item: String, amount: Double, unit: String) extends Entity

final case class Repair(id: Int, poolId: Int, repaired: Long, cost: Double, repair: String) extends Entity