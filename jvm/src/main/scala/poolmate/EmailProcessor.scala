package poolmate

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import javax.mail.Flags

import jodd.mail.{Email, ImapServer, MailServer, SmtpServer}
import jodd.mail.EmailFilter._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Using}

class EmailProcessor(conf: Config, store: Store) extends LazyLogging:
  private val host = conf.getString("email.host")
  private val to = conf.getString("email.to")
  private val password = conf.getString("email.password")
  private val subject = conf.getString("email.subject")

  private val imapServer: ImapServer = MailServer.create()
    .ssl(true)
    .host(host)
    .auth(to, password)
    .buildImapMailServer()

  def process(): Runnable =
    new Runnable() {
      override def run(): Unit =
        Using( imapServer.createSession ) { session =>
          session.open()

          val messages = session.receiveEmailAndMarkSeen( filter.flag(Flags.Flag.SEEN, false) )
          logger.info("*** EmailProcesor processed email and mark-seen messages: {}", messages.size)

          store.listUnprocessedEmails.foreach { email =>            
            messages.foreach { message =>
              logger.info("*** EmailProcesor subject {}", message.subject())
              logger.info("*** EmailProcesor message id: {}, email id: {}", message.messageId, email.id)

              if message.subject == subject && message.messageId() == email.id then
                store.processEmail( email.copy(processed = true, valid = true) )
                logger.info("*** EmailProcesor processed [valid] email: {}", email)

              else if message.subject != subject && message.messageId() == email.id then
                store.processEmail( email.copy(processed = true) )
                logger.warn("*** EmailProcesor processed [invalid] email: {}", email.id)
                store.removeAccount(email.license)
                logger.warn("*** EmailProcesor removed [invalid] account: {}", email.license)

              else logger.error("*** EmailProcesor [unknown] message: {}", message)
            }
          }
        }.get
    }