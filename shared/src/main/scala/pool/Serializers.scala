package pool

import scala.annotation.nowarn

object Serializers {
  import upickle.default._

  implicit val faultRW: ReadWriter[Fault] = macroRW

  implicit val commandRW: ReadWriter[Command] = ReadWriter.merge(
    macroRW[SignUp], macroRW[ActivateLicensee], macroRW[SignIn], macroRW[DeactivateLicensee]
  )

  implicit val signupRW: ReadWriter[SignUp] = macroRW
  implicit val activateLicenseeRW: ReadWriter[ActivateLicensee] = macroRW
  implicit val signinRW: ReadWriter[SignIn] = macroRW
  implicit val deactivateLicenseeRW: ReadWriter[DeactivateLicensee] = macroRW

  @nowarn implicit val eventRW: ReadWriter[Event] = ReadWriter.merge(
    macroRW[SignedUp], macroRW[LicenseeActivated], macroRW[SignedIn], macroRW[LicenseeDeactivated]
  )

  @nowarn implicit val signedUpRW: ReadWriter[SignedUp] = macroRW
  @nowarn implicit val licenseeActivatedRW: ReadWriter[LicenseeActivated] = macroRW
  @nowarn implicit val signedInRW: ReadWriter[SignedIn] = macroRW
  @nowarn implicit val licenseeDeactivatedRW: ReadWriter[LicenseeDeactivated] = macroRW

  @nowarn implicit val entityRW: ReadWriter[Entity] = ReadWriter.merge(
    macroRW[Licensee], macroRW[License], macroRW[Pool], macroRW[PoolId], macroRW[Surface], macroRW[Pump], macroRW[Timer],
    macroRW[TimerId], macroRW[TimerSetting], macroRW[Heater], macroRW[HeaterId], macroRW[HeaterSetting], macroRW[Measurement],
    macroRW[Cleaning], macroRW[Chemical], macroRW[Supply], macroRW[Repair]
  )

  implicit val licenseeRW: ReadWriter[Licensee] = macroRW
  @nowarn implicit val licenseRW: ReadWriter[License] = macroRW
  implicit val poolRW: ReadWriter[Pool] = macroRW
  @nowarn implicit val poolIdRW: ReadWriter[PoolId] = macroRW
  implicit val surfaceRW: ReadWriter[Surface] = macroRW
  implicit val pumpRW: ReadWriter[Pump] = macroRW
  implicit val timerRW: ReadWriter[Timer] = macroRW
  @nowarn implicit val timerIdRW: ReadWriter[TimerId] = macroRW
  implicit val timerSettingRW: ReadWriter[TimerSetting] = macroRW
  implicit val heaterRW: ReadWriter[Heater] = macroRW
  @nowarn implicit val heaterIdRW: ReadWriter[HeaterId] = macroRW
  implicit val heaterSettingRW: ReadWriter[HeaterSetting] = macroRW
  implicit val measurementRW: ReadWriter[Measurement] = macroRW
  implicit val cleaningRW: ReadWriter[Cleaning] = macroRW
  implicit val chemicalRW: ReadWriter[Chemical] = macroRW
  implicit val supplyRW: ReadWriter[Supply] = macroRW
  implicit val repairRW: ReadWriter[Repair] = macroRW

  @nowarn implicit val stateRW: ReadWriter[State] = ReadWriter.merge(
    macroRW[Id], macroRW[Count], macroRW[Pools], macroRW[Surfaces], macroRW[Pumps], macroRW[Timers], macroRW[TimerSettings],
    macroRW[Heaters], macroRW[HeaterSettings], macroRW[Measurements], macroRW[Cleanings], macroRW[Chemicals], macroRW[Supplies],
    macroRW[Repairs]
  )

  @nowarn implicit val idRW: ReadWriter[Id] = macroRW
  @nowarn implicit val countRW: ReadWriter[Count] = macroRW
  @nowarn implicit val poolsRW: ReadWriter[Pools] = macroRW
  @nowarn implicit val surfacesRW: ReadWriter[Surfaces] = macroRW
  @nowarn implicit val pumpsRW: ReadWriter[Pumps] = macroRW
  @nowarn implicit val timersRW: ReadWriter[Timers] = macroRW
  @nowarn implicit val timerSettingsRW: ReadWriter[TimerSettings] = macroRW
  @nowarn implicit val heatersRW: ReadWriter[Heaters] = macroRW
  @nowarn implicit val heaterSettingsRW: ReadWriter[HeaterSettings] = macroRW
  @nowarn implicit val measurementsRW: ReadWriter[Measurements] = macroRW
  @nowarn implicit val cleaningsRW: ReadWriter[Cleanings] = macroRW
  @nowarn implicit val chemicalsRW: ReadWriter[Chemicals] = macroRW
  @nowarn implicit val suppliesRW: ReadWriter[Supplies] = macroRW
  @nowarn implicit val repairsRW: ReadWriter[Repairs] = macroRW
}