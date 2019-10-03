package tripletail

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import jodd.mail.{ImapServer, MailServer, ReceiveMailSession}

import scala.concurrent.duration._
import scala.language.postfixOps

class EmailReceiver(conf: Config) extends Actor with ActorLogging {
  private val imapServer: ImapServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.imap.host"))
    .auth(conf.getString("email.imap.user"), conf.getString("email.imap.user"))
    .buildImapMailServer()

  private val folder = conf.getString("email.imap.folder")

  private def receiveEmail: Seq[String] = {
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

  private def processEmail(email: Seq[String]): Unit = {
    email foreach println
    ()
  }

  implicit val ec = context.dispatcher
  context.system.scheduler.schedule(initialDelay = 10 seconds, interval = 10 seconds)(processEmail(receiveEmail))

  override def receive: Receive = {
    case ReceiveEmail => processEmail(receiveEmail)
  }
}