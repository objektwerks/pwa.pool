package pool

import com.github.plokhotnyuk.jsoniter_scala.core._
import com.github.plokhotnyuk.jsoniter_scala.macros._

object Codecs {
  def fromSumTypeCodec[A <: B, B](sumTypeCodec: JsonValueCodec[B]): JsonValueCodec[A] =
    new JsonValueCodec[A] {
      override def decodeValue(in: JsonReader, default: A): A = sumTypeCodec.decodeValue(in, default).asInstanceOf[A]
      override def encodeValue(x: A, out: JsonWriter): Unit = sumTypeCodec.encodeValue(x, out)
      override val nullValue: A = null.asInstanceOf[A]
    }

  implicit val faultCodec: JsonValueCodec[Fault] = JsonCodecMaker.make
  implicit val commandCodec: JsonValueCodec[Command] = JsonCodecMaker.make
  implicit val eventCodec: JsonValueCodec[Event] = JsonCodecMaker.make
  implicit val entityCodec: JsonValueCodec[Entity] = JsonCodecMaker.make
  implicit val stateCodec: JsonValueCodec[State] = JsonCodecMaker.make

  object CommandCodecs {
    implicit val registerCodec: JsonValueCodec[Register] = fromSumTypeCodec(commandCodec)
    implicit val loginCodec: JsonValueCodec[Login] = fromSumTypeCodec(commandCodec)
    implicit val deactivateCodec: JsonValueCodec[Deactivate] = fromSumTypeCodec(commandCodec)
    implicit val reactivateCodec: JsonValueCodec[Reactivate] = fromSumTypeCodec(commandCodec)
  }

  object EventCodecs {
    implicit val registeringCodec: JsonValueCodec[Registering] = fromSumTypeCodec(eventCodec)
    implicit val loggedInCodec: JsonValueCodec[LoggedIn] = fromSumTypeCodec(eventCodec)
    implicit val deactivatedCodec: JsonValueCodec[Deactivated] = fromSumTypeCodec(eventCodec)
    implicit val reactivatedCodec: JsonValueCodec[Reactivated] = fromSumTypeCodec(eventCodec)
  }

  object EntityCodecs {
    implicit val accountCodec: JsonValueCodec[Account] = fromSumTypeCodec(entityCodec)
    implicit val licenseCodec: JsonValueCodec[License] = fromSumTypeCodec(entityCodec)
    implicit val poolCodec: JsonValueCodec[Pool] = fromSumTypeCodec(entityCodec)
    implicit val poolIdCode: JsonValueCodec[PoolId] = fromSumTypeCodec(entityCodec)
    implicit val surfaceCodec: JsonValueCodec[Surface] = fromSumTypeCodec(entityCodec)
    implicit val pumpCodec: JsonValueCodec[Pump] = fromSumTypeCodec(entityCodec)
    implicit val timerCodec: JsonValueCodec[Timer] = fromSumTypeCodec(entityCodec)
    implicit val timerIdCode: JsonValueCodec[TimerId] = fromSumTypeCodec(entityCodec)
    implicit val timerSettingCodec: JsonValueCodec[TimerSetting] = fromSumTypeCodec(entityCodec)
    implicit val heaterCodec: JsonValueCodec[Heater] = fromSumTypeCodec(entityCodec)
    implicit val heaterIdCode: JsonValueCodec[HeaterId] = fromSumTypeCodec(entityCodec)
    implicit val heaterSettingCodec: JsonValueCodec[HeaterSetting] = fromSumTypeCodec(entityCodec)
    implicit val measurementCodec: JsonValueCodec[Measurement] = fromSumTypeCodec(entityCodec)
    implicit val cleaningCodec: JsonValueCodec[Cleaning] = fromSumTypeCodec(entityCodec)
    implicit val chemicalCodec: JsonValueCodec[Chemical] = fromSumTypeCodec(entityCodec)
    implicit val supplyCodec: JsonValueCodec[Supply] = fromSumTypeCodec(entityCodec)
    implicit val repairCodec: JsonValueCodec[Repair] = fromSumTypeCodec(entityCodec)
  }

  object StateCodecs {
    implicit val idCodec: JsonValueCodec[Id] = fromSumTypeCodec(stateCodec)
    implicit val countCodec: JsonValueCodec[Count] = fromSumTypeCodec(stateCodec)
    implicit val poolsCodec: JsonValueCodec[Pools] = fromSumTypeCodec(stateCodec)
    implicit val surfacesCodec: JsonValueCodec[Surfaces] = fromSumTypeCodec(stateCodec)
    implicit val pumpsCodec: JsonValueCodec[Pumps] = fromSumTypeCodec(stateCodec)
    implicit val timersCodec: JsonValueCodec[Timers] = fromSumTypeCodec(stateCodec)
    implicit val timerSettingsCodec: JsonValueCodec[TimerSettings] = fromSumTypeCodec(stateCodec)
    implicit val heatersCodec: JsonValueCodec[Heaters] = fromSumTypeCodec(stateCodec)
    implicit val heaterSettingsCodec: JsonValueCodec[HeaterSettings] = fromSumTypeCodec(stateCodec)
    implicit val measurementsCodec: JsonValueCodec[Measurements] = fromSumTypeCodec(stateCodec)
    implicit val cleaningsCodec: JsonValueCodec[Cleanings] = fromSumTypeCodec(stateCodec)
    implicit val chemicalsCodec: JsonValueCodec[Chemicals] = fromSumTypeCodec(stateCodec)
    implicit val suppliesCodec: JsonValueCodec[Supplies] = fromSumTypeCodec(stateCodec)
    implicit val repairsCodec: JsonValueCodec[Repairs] = fromSumTypeCodec(stateCodec)
  }  
}