package pool

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._

object Codecs {
  implicit val faultCodec: JsonValueCodec[Fault] = JsonCodecMaker.make[Fault]

  implicit val commandCodec: JsonValueCodec[Command] = JsonCodecMaker.make[Command]
  implicit val registerCodec: JsonValueCodec[Register] = JsonCodecMaker.make[Register]
  implicit val loginCodec: JsonValueCodec[Login] = JsonCodecMaker.make[Login]
  implicit val deactivateCodec: JsonValueCodec[Deactivate] = JsonCodecMaker.make[Deactivate]
  implicit val reactivateCodec: JsonValueCodec[Reactivate] = JsonCodecMaker.make[Reactivate]

  implicit val eventCodec: JsonValueCodec[Event] = JsonCodecMaker.make[Event]
  implicit val registeringCodec: JsonValueCodec[Registering] = JsonCodecMaker.make[Registering]
  implicit val loggedInCodec: JsonValueCodec[LoggedIn] = JsonCodecMaker.make[LoggedIn]
  implicit val deactivatedCodec: JsonValueCodec[Deactivated] = JsonCodecMaker.make[Deactivated]
  implicit val reactivatedCodec: JsonValueCodec[Reactivated] = JsonCodecMaker.make[Reactivated]

  implicit val entityCodec: JsonValueCodec[Entity] = JsonCodecMaker.make[Entity]
  implicit val accountCodec: JsonValueCodec[Account] = JsonCodecMaker.make[Account]
  implicit val licenseCodec: JsonValueCodec[License] = JsonCodecMaker.make[License]
  implicit val poolCodec: JsonValueCodec[Pool] = JsonCodecMaker.make[Pool]
  implicit val poolIdCode: JsonValueCodec[PoolId] = JsonCodecMaker.make[PoolId]
  implicit val surfaceCodec: JsonValueCodec[Surface] = JsonCodecMaker.make[Surface]
  implicit val pumpCodec: JsonValueCodec[Pump] = JsonCodecMaker.make[Pump]
  implicit val timerCodec: JsonValueCodec[Timer] = JsonCodecMaker.make[Timer]
  implicit val timerIdCode: JsonValueCodec[TimerId] = JsonCodecMaker.make[TimerId]
  implicit val timerSettingCodec: JsonValueCodec[TimerSetting] = JsonCodecMaker.make[TimerSetting]
  implicit val heaterCodec: JsonValueCodec[Heater] = JsonCodecMaker.make[Heater]
  implicit val heaterIdCode: JsonValueCodec[HeaterId] = JsonCodecMaker.make[HeaterId]
  implicit val heaterSettingCodec: JsonValueCodec[HeaterSetting] = JsonCodecMaker.make[HeaterSetting]
  implicit val measurementCodec: JsonValueCodec[Measurement] = JsonCodecMaker.make[Measurement]
  implicit val cleaningCodec: JsonValueCodec[Cleaning] = JsonCodecMaker.make[Cleaning]
  implicit val chemicalCodec: JsonValueCodec[Chemical] = JsonCodecMaker.make[Chemical]
  implicit val supplyCodec: JsonValueCodec[Supply] = JsonCodecMaker.make[Supply]
  implicit val repairCodec: JsonValueCodec[Repair] = JsonCodecMaker.make[Repair]

  implicit val stateCodec: JsonValueCodec[State] = JsonCodecMaker.make[State]
  implicit val idCodec: JsonValueCodec[Id] = JsonCodecMaker.make[Id]
  implicit val countCodec: JsonValueCodec[Count] = JsonCodecMaker.make[Count]
  implicit val poolsCodec: JsonValueCodec[Pools] = JsonCodecMaker.make[Pools]
  implicit val surfacesCodec: JsonValueCodec[Surfaces] = JsonCodecMaker.make[Surfaces]
  implicit val pumpsCodec: JsonValueCodec[Pumps] = JsonCodecMaker.make[Pumps]
  implicit val timersCodec: JsonValueCodec[Timers] = JsonCodecMaker.make[Timers]
  implicit val timerSettingsCodec: JsonValueCodec[TimerSettings] = JsonCodecMaker.make[TimerSettings]
  implicit val heatersCodec: JsonValueCodec[Heaters] = JsonCodecMaker.make[Heaters]
  implicit val heaterSettingsCodec: JsonValueCodec[HeaterSettings] = JsonCodecMaker.make[HeaterSettings]
  implicit val measurementsCodec: JsonValueCodec[Measurements] = JsonCodecMaker.make[Measurements]
  implicit val cleaningsCodec: JsonValueCodec[Cleanings] = JsonCodecMaker.make[Cleanings]
  implicit val chemicalsCodec: JsonValueCodec[Chemicals] = JsonCodecMaker.make[Chemicals]
  implicit val suppliesCodec: JsonValueCodec[Supplies] = JsonCodecMaker.make[Supplies]
  implicit val repairsCodec: JsonValueCodec[Repairs] = JsonCodecMaker.make[Repairs]
}