package poolmate

sealed trait Event

final case class Authorized(license: String) extends Event
final case class Unauthorized(license: String) extends Event

final case class Explored(account: Account) extends Event
final case class LoggedIn(account: Account) extends Event

final case class Deactivated(account: Account) extends Event
final case class Reactivated(account: Account) extends Event

final case class Updated() extends Event

final case class PoolsListed(pools: List[Pool]) extends Event
final case class PoolAdded(pool: Pool) extends Event

final case class SurfacesListed(surfaces: List[Surface]) extends Event
final case class SurfaceAdded(surface: Surface) extends Event

final case class DecksListed(decks: List[Deck]) extends Event
final case class DeckAdded(deck: Deck) extends Event

final case class PumpsListed(pumps: List[Pump]) extends Event
final case class PumpAdded(pump: Pump) extends Event

final case class TimersListed(timers: List[Timer]) extends Event
final case class TimerAdded(timer: Timer) extends Event

final case class TimerSettingsListed(timerSettings: List[TimerSetting]) extends Event
final case class TimerSettingAdded(timerSetting: TimerSetting) extends Event

final case class HeatersListed(heaters: List[Heater]) extends Event
final case class HeaterAdded(heater: Heater) extends Event

final case class HeaterSettingsListed(heaterSettings: List[HeaterSetting]) extends Event
final case class HeaterSettingAdded(heaterSetting: HeaterSetting) extends Event

final case class MeasurementsListed(measurements: List[Measurement]) extends Event
final case class MeasurementAdded(measurement: Measurement) extends Event

final case class CleaningsListed(cleanings: List[Cleaning]) extends Event
final case class CleaningAdded(cleaning: Cleaning) extends Event

final case class ChemicalsListed(chemicals: List[Chemical]) extends Event
final case class ChemicalAdded(chemical: Chemical) extends Event

final case class SuppliesListed(supplies: List[Supply]) extends Event
final case class SupplyAdded(supply: Supply) extends Event

final case class RepairsListed(repairs: List[Repair]) extends Event
final case class RepairAdded(repair: Repair) extends Event

final case class Fault(dateOf: Int = DateTime.currentDate,
                       timeOf: Int = DateTime.currentTime,
                       nanoOf: Int = DateTime.nano,
                       cause: String) extends Event

object Fault:
  def apply(message: String): Fault = Fault(cause = message)
  def apply(throwable: Throwable): Fault = Fault(cause = throwable.getMessage)