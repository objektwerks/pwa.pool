package tripletail

sealed trait Message extends Product with Serializable

case class SendEmail(to: String, license: String)

case object ReceiveEmail