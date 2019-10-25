package tripletail

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

  implicit val eventRW: ReadWriter[Event] = ReadWriter.merge(
    macroRW[SignedUp], macroRW[LicenseeActivated], macroRW[SignedIn], macroRW[LicenseeDeactivated]
  )

  implicit val signedUpRW: ReadWriter[SignedUp] = macroRW
  implicit val licenseeActivatedRW: ReadWriter[LicenseeActivated] = macroRW
  implicit val signedInRW: ReadWriter[SignedIn] = macroRW
  implicit val licenseeDeactivatedRW: ReadWriter[LicenseeDeactivated] = macroRW

  implicit val entityRW: ReadWriter[Entity] = ReadWriter.merge(
    macroRW[Licensee], macroRW[License], macroRW[Pool], macroRW[PoolId], macroRW[Surface], macroRW[Pump], macroRW[Timer],
    macroRW[TimerId], macroRW[TimerSetting], macroRW[Heater], macroRW[HeaterId], macroRW[HeaterSetting], macroRW[Measurement],
    macroRW[Cleaning], macroRW[Chemical], macroRW[Supply], macroRW[Repair]
  )

  implicit val licenseeRW: ReadWriter[Licensee] = macroRW
  implicit val licenseRW: ReadWriter[License] = macroRW
  implicit val poolRW: ReadWriter[Pool] = macroRW
  implicit val poolIdRW: ReadWriter[PoolId] = macroRW
  implicit val surfaceRW: ReadWriter[Surface] = macroRW
  implicit val pumpRW: ReadWriter[Pump] = macroRW
  implicit val timerRW: ReadWriter[Timer] = macroRW
  implicit val timerIdRW: ReadWriter[TimerId] = macroRW
  implicit val timerSettingRW: ReadWriter[TimerSetting] = macroRW
  implicit val heaterRW: ReadWriter[Heater] = macroRW
  implicit val heaterIdRW: ReadWriter[HeaterId] = macroRW
  implicit val heaterSettingRW: ReadWriter[HeaterSetting] = macroRW
  implicit val measurementRW: ReadWriter[Measurement] = macroRW
  implicit val cleaningRW: ReadWriter[Cleaning] = macroRW
  implicit val chemicalRW: ReadWriter[Chemical] = macroRW
  implicit val supplyRW: ReadWriter[Supply] = macroRW
  implicit val repairRW: ReadWriter[Repair] = macroRW

  implicit val stateRW: ReadWriter[State] = ReadWriter.merge(
    macroRW[Id], macroRW[Count], macroRW[Pools], macroRW[Surfaces], macroRW[Pumps], macroRW[Timers], macroRW[TimerSettings],
    macroRW[Heaters], macroRW[HeaterSettings], macroRW[Measurements], macroRW[Cleanings], macroRW[Chemicals], macroRW[Supplies],
    macroRW[Repairs]
  )

  implicit val idRW: ReadWriter[Id] = macroRW
  implicit val countRW: ReadWriter[Count] = macroRW
  implicit val poolsRW: ReadWriter[Pools] = macroRW
  implicit val surfacesRW: ReadWriter[Surfaces] = macroRW
  implicit val pumpsRW: ReadWriter[Pumps] = macroRW
  implicit val timersRW: ReadWriter[Timers] = macroRW
  implicit val timerSettingsRW: ReadWriter[TimerSettings] = macroRW
  implicit val heatersRW: ReadWriter[Heaters] = macroRW
  implicit val heaterSettingsRW: ReadWriter[HeaterSettings] = macroRW
  implicit val measurementsRW: ReadWriter[Measurements] = macroRW
  implicit val cleaningsRW: ReadWriter[Cleanings] = macroRW
  implicit val chemicalsRW: ReadWriter[Chemicals] = macroRW
  implicit val suppliesRW: ReadWriter[Supplies] = macroRW
  implicit val repairsRW: ReadWriter[Repairs] = macroRW
}