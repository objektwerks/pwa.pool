package pool

object Serializers {
  import upickle.default._

  implicit val faultRW: ReadWriter[Fault] = macroRW

  implicit val registerRW: ReadWriter[Register] = macroRW
  implicit val loginRW: ReadWriter[Login] = macroRW
  implicit val deactivateRW: ReadWriter[Deactivate] = macroRW
  implicit val reactivateRW: ReadWriter[Reactivate] = macroRW

  implicit val commandRW: ReadWriter[Command] = ReadWriter.merge(
    registerRW, loginRW, deactivateRW, reactivateRW
  )

  implicit val registeringRW: ReadWriter[Registering] = macroRW
  implicit val loggedInRW: ReadWriter[LoggedIn] = macroRW
  implicit val deactivatedRW: ReadWriter[Deactivated] = macroRW
  implicit val reactivatedRW: ReadWriter[Reactivated] = macroRW

  implicit val eventRW: ReadWriter[Event] = ReadWriter.merge(
    registeringRW, loggedInRW, deactivatedRW, reactivatedRW
  )

  implicit val accountRW: ReadWriter[Account] = macroRW
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

  implicit val entityRW: ReadWriter[Entity] = ReadWriter.merge(
    accountRW, licenseRW, poolRW, poolIdRW, surfaceRW, pumpRW, timerRW, timerIdRW, timerSettingRW,
    heaterRW, heaterIdRW, heaterSettingRW, measurementRW, cleaningRW, chemicalRW, supplyRW, repairRW
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

  implicit val stateRW: ReadWriter[State] = ReadWriter.merge(
    idRW, countRW, poolsRW, surfacesRW, pumpsRW, timersRW, timerSettingsRW, heatersRW,
    heaterSettingsRW, measurementsRW, cleaningsRW, chemicalsRW, suppliesRW, repairsRW
  )
}