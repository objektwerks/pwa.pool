package tripletail

object Serializers {
  import upickle.default._

  implicit val faultRW: ReadWriter[Fault] = macroRW

  implicit val entityRW: ReadWriter[Entity] = ReadWriter.merge(
    macroRW[SignUp], macroRW[SignIn], macroRW[Licensee], macroRW[License], macroRW[Pool], macroRW[PoolId], macroRW[Surface],
    macroRW[Pump], macroRW[Timer], macroRW[TimerId], macroRW[TimerSetting], macroRW[Heater], macroRW[HeaterId], macroRW[HeaterSetting],
    macroRW[Cleaning], macroRW[Measurement], macroRW[Chemical], macroRW[Supply], macroRW[Repair]
  )

  implicit val signupRW: ReadWriter[SignUp] = macroRW
  implicit val signinRW: ReadWriter[SignIn] = macroRW
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
  implicit val cleaningRW: ReadWriter[Cleaning] = macroRW
  implicit val measurementRW: ReadWriter[Measurement] = macroRW
  implicit val chemicalRW: ReadWriter[Chemical] = macroRW
  implicit val supplyRW: ReadWriter[Supply] = macroRW
  implicit val repairRW: ReadWriter[Repair] = macroRW

  implicit val stateRW: ReadWriter[State] = ReadWriter.merge(
    macroRW[SignedUp], macroRW[SignedIn], macroRW[Id], macroRW[Count], macroRW[Pools], macroRW[Surfaces], macroRW[Pumps],
    macroRW[Timers], macroRW[TimerSettings], macroRW[Heaters], macroRW[HeaterSettings], macroRW[Cleanings], macroRW[Measurements],
    macroRW[Chemicals], macroRW[Supplies], macroRW[Repairs]
  )

  implicit val signedUpRW: ReadWriter[SignedUp] = macroRW
  implicit val signedInRW: ReadWriter[SignedIn] = macroRW
  implicit val idRW: ReadWriter[Id] = macroRW
  implicit val countRW: ReadWriter[Count] = macroRW
  implicit val poolsRW: ReadWriter[Pools] = macroRW
  implicit val surfacesRW: ReadWriter[Surfaces] = macroRW
  implicit val pumpsRW: ReadWriter[Pumps] = macroRW
  implicit val timersRW: ReadWriter[Timers] = macroRW
  implicit val timerSettingsRW: ReadWriter[TimerSettings] = macroRW
  implicit val heatersRW: ReadWriter[Heaters] = macroRW
  implicit val heaterSettingsRW: ReadWriter[HeaterSettings] = macroRW
  implicit val cleaningsRW: ReadWriter[Cleanings] = macroRW
  implicit val measurementsRW: ReadWriter[Measurements] = macroRW
  implicit val chemicalsRW: ReadWriter[Chemicals] = macroRW
  implicit val suppliesRW: ReadWriter[Supplies] = macroRW
  implicit val repairsRW: ReadWriter[Repairs] = macroRW
}