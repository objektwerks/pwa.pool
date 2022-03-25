package pool

import akka.actor.Actor

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import javax.mail.Flags

import jodd.mail.{Email, ImapServer, MailServer, SmtpServer}
import jodd.mail.EmailFilter._

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Using}

final class Emailer(conf: Config,
                    store: Store) extends Actor with LazyLogging {
  implicit private val ec = context.system.dispatcher

  private val host = conf.getString("email.host")
  private val user = conf.getString("email.user")
  private val password = conf.getString("email.password")
  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val message = conf.getString("email.message")
  private val email = conf.getString("email.email")
  private val lic = conf.getString("email.lic")
  private val pin = conf.getString("email.pin")
  private val instructions = conf.getString("email.instructions")

  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(host)
    .auth(user, password)
    .buildSmtpMailServer()

  private val imapServer: ImapServer = MailServer.create()
    .ssl(true)
    .host(host)
    .auth(user, password)
    .buildImapMailServer()

  context.system.scheduler.scheduleWithFixedDelay(10 seconds, 10 seconds)( receiveEmail() )

  private def buildEmail(account: Account): Email = {
    val html = s"""
                  |<!DOCTYPE html>
                  |<html lang="en">
                  |<head>
                  |<meta charset="utf-8">
                  |<title>$subject</title>
                  |</head>
                  |<body>
                  |<p>$message</p>
                  |<p>$lic ${account.license}</p>
                  |<p>$email ${account.email}</p>
                  |<p>$pin ${account.pin}</p>
                  |<p>$instructions</p>
                  |</body>
                  |</html>
                  |""".stripMargin
    Email.create()
      .from(from)
      .to(account.email)
      .subject(subject)
      .htmlMessage(html, "UTF-8")
  }

  private def sendEmail(register: Register): Unit =
    Using( smtpServer.createSession ) { session =>
      session.open()
      if (session.isConnected) {
        val account = Account(register.email)
        val messageId = session.sendMail( buildEmail(account) )
        val email = pool.Email(id = messageId, license = account.license, address = account.email)
        logger.info("*** Emailer sent: {}", email)
        store.addEmail(email)
        logger.info("*** Emailer added: {}", email)
        store.registerAccount(account).recoverWith { case _ => store.registerAccount(account) } // retry once
        logger.info("*** Emailer registered account: {}", account)
      } else logger.error("*** Emailer smtp server session is NOT connected!")
      ()
    }.get

  private def receiveEmail(): Runnable = new Runnable() {
    override def run(): Unit =
      Using( imapServer.createSession ) { session =>
        session.open()
        if (session.isConnected) {
          val messages = session.receiveEmailAndMarkSeen( filter.flag(Flags.Flag.SEEN, false) )
          logger.info("*** Emailer receiveEmailAndMarkSeen messages: {}", messages.size)
          store.listEmails.onComplete {
            case Success(emails) =>
              logger.info("*** Emailer store.listEmails: {}", emails.size)
              emails.foreach { email =>
                messages.foreach { message =>
                  logger.info("*** Emailer subject {}", message.subject())
                  logger.info("*** Emailer message id: {}, email id: {}", message.messageId, email.id)
                  if ( message.subject != subject && message.messageId() == email.id ) {
                    store.updateEmail( email.copy(processed = true) )
                    logger.info("*** Emailer [invalid] updateEmail: {}", email.id)
                    store.removeAccount( email.license )
                    logger.info("*** Emailer removeAccount: {}", email.license)
                  } else if ( message.messageId() == email.id ) {
                    store.updateEmail( email.copy(processed = true, valid = true) )
                    logger.info("*** Emailer [valid] updateEmail: {}", email.id)
                  } else logger.info("*** Emailer invalid message: {}", message.messageId())
                }
              }
            case Failure(error) => logger.error("*** Emailer store.listEmails failed: {}", error)
          }
        } else logger.error("*** Emailer imap server session is NOT connected!")
        ()
      }.get
  }

  override def receive: Receive = {
    case register: Register => sendEmail(register)
  }
}