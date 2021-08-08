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

}