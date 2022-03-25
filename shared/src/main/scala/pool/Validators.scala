package pool

object Validators {
  implicit class StringOps(val value: String) {
    def isLicense: Boolean = if (value.nonEmpty) value.length == 36 else false
    def isEmail: Boolean = value.nonEmpty && value.length >=3 && value.contains("@")
    def isPin: Boolean = value.length == 9
  }

  implicit class RegisterOps(val register: Register) {
    def isValid: Boolean = register.email.isEmail
  }

  implicit class LoginOps(val login: Login) {
    def isValid: Boolean = login.email.isEmail && login.pin.isPin
  }

  implicit class DeactivateOps(val deactivate: Deactivate) {
    def isValid: Boolean = deactivate.license.isLicense
  }

  implicit class ReactivateOps(val reactivate: Reactivate) {
    def isValid: Boolean = reactivate.license.isLicense
  }

  implicit class AccountOps(val account: Account) {
    def isActivated: Boolean =
      account.license.isLicense &&
      account.email.isEmail &&
      account.pin.isPin &&
      account.activated > 0 &&
      account.deactivated == 0
    def isDeactivated: Boolean =
      account.license.isLicense &&
      account.email.isEmail &&
      account.pin.isPin &&
      account.activated == 0 &&
      account.deactivated > 0
  }

  implicit class LicenseOps(val license: License) {
    def isValid: Boolean = license.key.isLicense
  }

  implicit class PoolOps(val pool: Pool) {
    def isValid: Boolean =
      pool.id >= 0 &&
      pool.license.isLicense &&
      pool.name.nonEmpty &&
      pool.built > 0 &&
      pool.volume >= 1000
  }

  implicit class PoolIdOps(val poolId: PoolId) {
    def isValid: Boolean = poolId.id > 0
  }

  implicit class SurfaceOps(val surface: Surface) {
    def isValid: Boolean =
      surface.id >= 0 &&
      surface.poolId > 0 &&
      surface.installed > 0 &&
      surface.kind.nonEmpty
  }

  implicit class PumpOps(val pump: Pump) {
    def isValid: Boolean =
      pump.id >= 0 &&
      pump.poolId > 0 &&
      pump.installed > 0 &&
      pump.model.nonEmpty
  }

  implicit class TimerOps(val timer: Timer) {
    def isValid: Boolean =
      timer.id >= 0 &&
      timer.poolId > 0 &&
      timer.installed > 0 &&
      timer.model.nonEmpty
  }

  implicit class TimerIdOps(val timerId: TimerId) {
    def isValid: Boolean = timerId.id > 0
  }

  implicit class TimerSettingOps(val timerSetting: TimerSetting) {
    def isValid: Boolean =
      timerSetting.id >= 0 &&
      timerSetting.timerId > 0 &&
      timerSetting.created > 0 &&
      timerSetting.timeOn > 0 &&
      timerSetting.timeOff > 0 &&
      timerSetting.timeOff > timerSetting.timeOn
  }

  implicit class HeaterOps(val heater: Heater) {
    def isValid: Boolean =
      heater.id >= 0 &&
      heater.poolId > 0 &&
      heater.installed > 0 &&
      heater.model.nonEmpty
  }

  implicit class HeaterIdOps(val heaterId: HeaterId) {
    def isValid: Boolean = heaterId.id > 0
  }

  implicit class HeaterSettingOps(val heaterSetting: HeaterSetting) {
    def isValid: Boolean =
      heaterSetting.id >= 0 &&
      heaterSetting.heaterId > 0 &&
      heaterSetting.temp > 0 &&
      heaterSetting.dateOn > 0 &&
      heaterSetting.dateOff >= 0
  }

  implicit class MeasurementOps(val measurement: Measurement) {
    private val temp = 0 to 100
    private val totalHardness = 1 to 1000
    private val totalChlorine = 0 to 10
    private val totalBromine = 0 to 20
    private val freeChlorine = 0 to 10
    private val totalAlkalinity = 0 to 240
    private val cyanuricAcid = 0 to 300
    def isValid: Boolean =
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

  implicit class CleaningOps(val cleaning: Cleaning) {
    def isValid: Boolean =
      cleaning.id >= 0 &&
      cleaning.poolId > 0 &&
      cleaning.cleaned > 0
  }

  implicit class ChemicalOps(val chemical: Chemical) {
    def isValid: Boolean =
      chemical.id >= 0 &&
      chemical.poolId > 0 &&
      chemical.added > 0 &&
      chemical.chemical.nonEmpty &&
      chemical.amount > 0.00 &&
      chemical.unit.nonEmpty
  }

  implicit class SupplyOps(val supply: Supply) {
    def isValid: Boolean =
      supply.id >= 0 &&
      supply.poolId > 0 &&
      supply.purchased > 0 &&
      supply.cost > 0.00 &&
      supply.item.nonEmpty &&
      supply.amount > 0.00 &&
      supply.unit.nonEmpty
  }

  implicit class RepairOps(val repair: Repair) {
    def isValid: Boolean =
      repair.id >= 0 &&
      repair.poolId > 0 &&
      repair.repaired > 0 &&
      repair.cost > 0.00 &&
      repair.repair.nonEmpty
  }
}