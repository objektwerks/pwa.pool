package tripletail

import com.typesafe.config.Config
import jodd.mail.{Email, ImapServer, MailServer, ReceiveMailSession, SmtpServer}

object Emailer {
  def apply(conf: Config): Emailer = new Emailer(conf)
}

class Emailer(conf: Config) {
  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.smtp.host"))
    .auth(conf.getString("email.smtp.user"), conf.getString("email.smtp.user"))
    .buildSmtpMailServer()

  private val imapServer: ImapServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.imap.host"))
    .auth(conf.getString("email.imap.user"), conf.getString("email.imap.user"))
    .buildImapMailServer()

  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val message = conf.getString("email.message")
  private val folder = conf.getString("email.imap.folder")

  private def buildEmail(to: String, license: String): Email = Email.create()
    .from(from)
    .to(to)
    .subject(subject)
    .textMessage(s"$message: $license")

  def ping(): Unit = ()

  def send(to: String, license: String): Unit = {
    val email: Email = buildEmail(to, license)
    val session = smtpServer.createSession
    session.open()
    session.sendMail(email)
    session.close()
  }

  def retrieve: Seq[String] = {
    val session: ReceiveMailSession = imapServer.createSession()
    session.open()
    val receivedEmail = session
      .receive()
      .markSeen()
      .fromFolder(folder)
      .get()
    session.close()
    receivedEmail.map(re => re.from.getEmail)
  }
}