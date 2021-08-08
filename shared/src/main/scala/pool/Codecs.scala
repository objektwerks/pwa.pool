package pool

object Codecs {
  import com.github.plokhotnyuk.jsoniter_scala.core._
  import com.github.plokhotnyuk.jsoniter_scala.macros._

  implicit val faultCodec: JsonValueCodec[Fault] = JsonCodecMaker.make

  implicit val commandCodec: JsonValueCodec[Command] = JsonCodecMaker.make
  implicit val registerCodec: JsonValueCodec[Register] = JsonCodecMaker.make
  implicit val loginCodec: JsonValueCodec[Login] = JsonCodecMaker.make
  implicit val deactivateCodec: JsonValueCodec[Deactivate] = JsonCodecMaker.make
  implicit val reactivateCodec: JsonValueCodec[Reactivate] = JsonCodecMaker.make

  implicit val eventCodec: JsonValueCodec[Event] = JsonCodecMaker.make
  implicit val registeringCodec: JsonValueCodec[Registering] = JsonCodecMaker.make
  implicit val loggedInCodec: JsonValueCodec[LoggedIn] = JsonCodecMaker.make
  implicit val deactivatedCodec: JsonValueCodec[Deactivated] = JsonCodecMaker.make
  implicit val reactivatedCodec: JsonValueCodec[Reactivated] = JsonCodecMaker.make

  implicit val entityCodec: JsonValueCodec[Entity] = JsonCodecMaker.make
  implicit val accountCodec: JsonValueCodec[Account] = JsonCodecMaker.make
  implicit val licenseCodec: JsonValueCodec[License] = JsonCodecMaker.make
  implicit val poolCodec: JsonValueCodec[Pool] = JsonCodecMaker.make
  implicit val surfaceCodec: JsonValueCodec[Surface] = JsonCodecMaker.make
  implicit val pumpCodec: JsonValueCodec[Pump] = JsonCodecMaker.make
  implicit val timerCodec: JsonValueCodec[Timer] = JsonCodecMaker.make
  implicit val timerSettingCodec: JsonValueCodec[TimerSetting] = JsonCodecMaker.make
  implicit val heaterCodec: JsonValueCodec[Heater] = JsonCodecMaker.make
  implicit val heaterSettingCodec: JsonValueCodec[HeaterSetting] = JsonCodecMaker.make
  implicit val measurementCodec: JsonValueCodec[Measurement] = JsonCodecMaker.make
  implicit val cleaningCodec: JsonValueCodec[Cleaning] = JsonCodecMaker.make
  implicit val chemicalCodec: JsonValueCodec[Chemical] = JsonCodecMaker.make
  implicit val supplyCodec: JsonValueCodec[Supply] = JsonCodecMaker.make

  
}