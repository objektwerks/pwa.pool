package tripletail

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import jodd.mail.{Email, MailServer, SmtpServer}

class EmailSender(conf: Config) extends Actor with ActorLogging {
  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.smtp.host"))
    .auth(conf.getString("email.smtp.user"), conf.getString("email.smtp.user"))
    .buildSmtpMailServer()

  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val message = conf.getString("email.message")

  private def buildEmail(to: String, license: String): Email = Email.create()
    .from(from)
    .to(to)
    .subject(subject)
    .textMessage(s"$message: $license")

  private def sendEmail(to: String, license: String): Unit = {
    val email: Email = buildEmail(to, license)
    val session = smtpServer.createSession
    session.open()
    session.sendMail(email)
    session.close()
  }

  override def receive: Receive = {
    case send: SendEmail => sendEmail(send.to, send.license)
  }
}