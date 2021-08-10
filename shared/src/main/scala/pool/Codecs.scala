package pool

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._

object Codecs {
  implicit val faultCodec: JsonValueCodec[Fault] = JsonCodecMaker.make
  implicit val commandCodec: JsonValueCodec[Command] = JsonCodecMaker.make
  implicit val eventCodec: JsonValueCodec[Event] = JsonCodecMaker.make
  implicit val entityCodec: JsonValueCodec[Entity] = JsonCodecMaker.make
  implicit val stateCodec: JsonValueCodec[State] = JsonCodecMaker.make

  object CommandCodecs {
    implicit val registerCodec: JsonValueCodec[Register] = JsonCodecMaker.make
    implicit val loginCodec: JsonValueCodec[Login] = JsonCodecMaker.make
    implicit val deactivateCodec: JsonValueCodec[Deactivate] = JsonCodecMaker.make
    implicit val reactivateCodec: JsonValueCodec[Reactivate] = JsonCodecMaker.make
  }

  object EventCodecs {
    implicit val registeringCodec: JsonValueCodec[Registering] = JsonCodecMaker.make
    implicit val loggedInCodec: JsonValueCodec[LoggedIn] = JsonCodecMaker.make
    implicit val deactivatedCodec: JsonValueCodec[Deactivated] = JsonCodecMaker.make
    implicit val reactivatedCodec: JsonValueCodec[Reactivated] = JsonCodecMaker.make
  }

  object EntityCodecs {
    implicit val accountCodec: JsonValueCodec[Account] = JsonCodecMaker.make
    implicit val licenseCodec: JsonValueCodec[License] = JsonCodecMaker.make
    implicit val poolCodec: JsonValueCodec[Pool] = JsonCodecMaker.make
    implicit val poolIdCode: JsonValueCodec[PoolId] = JsonCodecMaker.make
    implicit val surfaceCodec: JsonValueCodec[Surface] = JsonCodecMaker.make
    implicit val pumpCodec: JsonValueCodec[Pump] = JsonCodecMaker.make
    implicit val timerCodec: JsonValueCodec[Timer] = JsonCodecMaker.make
    implicit val timerIdCode: JsonValueCodec[TimerId] = JsonCodecMaker.make
    implicit val timerSettingCodec: JsonValueCodec[TimerSetting] = JsonCodecMaker.make
    implicit val heaterCodec: JsonValueCodec[Heater] = JsonCodecMaker.make
    implicit val heaterIdCode: JsonValueCodec[HeaterId] = JsonCodecMaker.make
    implicit val heaterSettingCodec: JsonValueCodec[HeaterSetting] = JsonCodecMaker.make
    implicit val measurementCodec: JsonValueCodec[Measurement] = JsonCodecMaker.make
    implicit val cleaningCodec: JsonValueCodec[Cleaning] = JsonCodecMaker.make
    implicit val chemicalCodec: JsonValueCodec[Chemical] = JsonCodecMaker.make
    implicit val supplyCodec: JsonValueCodec[Supply] = JsonCodecMaker.make
    implicit val repairCodec: JsonValueCodec[Repair] = JsonCodecMaker.make
  }

  object StateCodecs {
    implicit val idCodec: JsonValueCodec[Id] = JsonCodecMaker.make
    implicit val countCodec: JsonValueCodec[Count] = JsonCodecMaker.make
    implicit val poolsCodec: JsonValueCodec[Pools] = JsonCodecMaker.make
    implicit val surfacesCodec: JsonValueCodec[Surfaces] = JsonCodecMaker.make
    implicit val pumpsCodec: JsonValueCodec[Pumps] = JsonCodecMaker.make
    implicit val timersCodec: JsonValueCodec[Timers] = JsonCodecMaker.make
    implicit val timerSettingsCodec: JsonValueCodec[TimerSettings] = JsonCodecMaker.make
    implicit val heatersCodec: JsonValueCodec[Heaters] = JsonCodecMaker.make
    implicit val heaterSettingsCodec: JsonValueCodec[HeaterSettings] = JsonCodecMaker.make
    implicit val measurementsCodec: JsonValueCodec[Measurements] = JsonCodecMaker.make
    implicit val cleaningsCodec: JsonValueCodec[Cleanings] = JsonCodecMaker.make
    implicit val chemicalsCodec: JsonValueCodec[Chemicals] = JsonCodecMaker.make
    implicit val suppliesCodec: JsonValueCodec[Supplies] = JsonCodecMaker.make
    implicit val repairsCodec: JsonValueCodec[Repairs] = JsonCodecMaker.make
  }  
}