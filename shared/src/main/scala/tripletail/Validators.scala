package tripletail

object Validators {
  implicit class StringOps(val value: String) {
    def <(length: Int): Boolean = if (value.nonEmpty) value.length < length else false
    def <=(length: Int): Boolean = if (value.nonEmpty) value.length <= length else false
    def ===(length: Int): Boolean = if (value.nonEmpty) value.length == length else false
    def >(length: Int): Boolean = if (value.nonEmpty) value.length > length else false
    def >=(length: Int): Boolean = if (value.nonEmpty) value.length >= length else false
  }

  implicit class SignUpOps(val signup: SignUp) {
    def isInvalid: Boolean = signup.email.isEmpty
  }

  implicit class SignInOps(val signin: SignIn) {
    def isInvalid: Boolean = !(signin.license === 36 && signin.email.nonEmpty)
  }

  implicit class LicenseeOps(val licensee: Licensee) {
    private def predicate: Boolean = {
      licensee.license === 36 &&
      licensee.email.nonEmpty &&
      licensee.activated <= DateTime.currentDate
    }
    def isValid = predicate
    def isInvalid: Boolean = !predicate
  }

  implicit class PoolOps(val pool: Pool) {
    def isInvalid: Boolean = !{
      pool.id >= 0 &&
      pool.license === 36 &&
      pool.built > 0 &&
      (pool.lat >= -90.000000 && pool.lat <= 90.000000) &&
      (pool.lat >= -180.000000 && pool.lat <= 180.000000) &&
      pool.volume >= 1000
    }
  }

  implicit class PoolIdOps(val poolId: PoolId) {
    def isInvalid: Boolean = !{ poolId.id > 0 }
  }

  implicit class SurfaceOps(val surface: Surface) {
    def isInvalid: Boolean = !{
      surface.id >= 0 &&
      surface.poolId > 0 &&
      surface.installed > 0 &&
      surface.kind.nonEmpty
    }
  }

  implicit class PumpOps(val pump: Pump) {
    def isInvalid: Boolean = !{
      pump.id >= 0 &&
      pump.poolId > 0 &&
      pump.installed > 0 &&
      pump.model.nonEmpty
    }
  }

  implicit class TimerOps(val timer: Timer) {
    def isInvalid: Boolean = !{
      timer.id >= 0 &&
      timer.poolId > 0 &&
      timer.installed > 0 &&
      timer.model.nonEmpty
    }
  }

  implicit class TimerIdOps(val timerId: TimerId) {
    def isInvalid: Boolean = !{ timerId.id > 0 }
  }

  implicit class TimerSettingOps(val timerSetting: TimerSetting) {
    def isInvalid: Boolean = !{
      timerSetting.id >= 0 &&
      timerSetting.timerId > 0 &&
      timerSetting.created > 0 &&
      timerSetting.timeOn > 0 &&
      timerSetting.timeOff > 0 &&
      timerSetting.timeOff > timerSetting.timeOn
    }
  }

  implicit class HeaterOps(val heater: Heater) {
    def isInvalid: Boolean = !{
      heater.id >= 0 &&
      heater.poolId > 0 &&
      heater.installed > 0 &&
      heater.model.nonEmpty
    }
  }

  implicit class HeaterIdOps(val heaterId: HeaterId) {
    def isInvalid: Boolean = !{ heaterId.id > 0 }
  }

  implicit class HeaterSettingOps(val heaterSetting: HeaterSetting) {
    def isInvalid: Boolean = !{
      heaterSetting.id >= 0 &&
      heaterSetting.heaterId > 0 &&
      heaterSetting.temp > 0 &&
      heaterSetting.dateOn > 0 &&
      heaterSetting.timeOn > 0 &&
      heaterSetting.timeOff > 0
    }
  }

  implicit class CleaningOps(val cleaning: Cleaning) {
    def isInvalid: Boolean = !{
      cleaning.id >= 0 &&
      cleaning.poolId > 0 &&
      cleaning.cleaned > 0 &&
      cleaning.brush &&
      cleaning.net &&
      cleaning.vacuum &&
      cleaning.skimmerBasket &&
      cleaning.pumpBasket &&
      cleaning.pumpFilter &&
      cleaning.pumpChlorineTablets >= 0
    }
  }

  implicit class MeasurementOps(val measurement: Measurement) {
    private val temp = 32 to 95
    private val totalHardness = 1 to 1000
    private val totalChlorine = 0 to 10
    private val totalBromine = 0 to 20
    private val freeChlorine = 0 to 10
    private val totalAlkalinity = 0 to 240
    private val cyanuricAcid = 0 to 300
    def isInvalid: Boolean = !{
      measurement.id >= 0 &&
      measurement.poolId > 0 &&
      measurement.measured > 0 &&
      temp.contains(measurement.temp) &&
      totalHardness.contains(measurement.totalHardness) &&
      totalChlorine.contains(measurement.totalChlorine) &&
      totalBromine.contains(measurement.totalBromine) &&
      freeChlorine.contains(measurement.freeChlorine) &&
      (measurement.ph >= 6.2 && measurement.ph <= 8.4) &&
      totalAlkalinity.contains(measurement.totalAlkalinity) &&
      cyanuricAcid.contains(measurement.cyanuricAcid)
    }
  }

  implicit class ChemicalOps(val chemical: Chemical) {
    def isInvalid: Boolean = !{
      chemical.id >= 0 &&
      chemical.poolId > 0 &&
      chemical.added > 0 &&
      chemical.chemical.nonEmpty &&
      chemical.amount > 0.00 &&
      chemical.unit.nonEmpty
    }
  }

  implicit class SupplyOps(val supply: Supply) {
    def isInvalid: Boolean = !{
      supply.id >= 0 &&
      supply.poolId > 0 &&
      supply.purchased > 0 &&
      supply.cost > 0.00 &&
      supply.item.nonEmpty &&
      supply.amount > 0.00 &&
      supply.unit.nonEmpty
    }
  }

  implicit class RepairOps(val repair: Repair) {
    def isInvalid: Boolean = !{
      repair.id >= 0 &&
      repair.poolId > 0 &&
      repair.repaired > 0 &&
      repair.cost > 0.00 &&
      repair.repair.nonEmpty
    }
  }
}