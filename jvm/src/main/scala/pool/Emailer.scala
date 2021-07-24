package pool

import akka.actor.Actor

import com.typesafe.config.Config

import jodd.mail.{Email, ImapServer, MailServer, SmtpServer}
import jodd.mail.EmailFilter._

import org.slf4j.Logger

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Using}

final class Emailer(conf: Config,
                    store: Store,
                    logger: Logger) extends Actor {
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
    .host(host)
    .ssl(true)
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
      val account = Account(register.email)
      val messageId = session.sendMail( buildEmail(account) )
      val email = pool.Email(id = messageId, license = account.license, address = account.email)
      logger.info("*** Emailer sent: {}", email)
      store.addEmail(email)
      logger.info("*** Emailer added: {}", email)
      store.registerAccount(account)
      logger.info("*** Emailer registered account: {}", account)
      ()
    }.get

  private def receiveEmail(): Runnable = new Runnable() {
    override def run(): Unit =
      Using( imapServer.createSession ) { session =>
        session.open()
        store.listEmails.onComplete {
          case Success(emails) =>
            logger.info("*** Emailer listEmails [{}]: {}", emails.size, emails.foreach(println))
            emails.foreach { email =>
              val messages = session.receiveEmailAndDelete( filter().messageId(email.id) )
              logger.info("*** Emailer receiveEmailAndDelete [{}] messages [{}]: {}", email.id, messages.size, messages.foreach(println))
              messages.foreach { message =>
                logger.info("*** Emailer message id [{}] : email id [{}]", message.messageId, email.id)
                if ( message.subject.contains("Mail delivery failed") && message.messageId() == email.id ) {
                  store.updateEmail( email.copy(processed = true) )
                  logger.info("*** Emailer updateEmail: {}", email)
                  store.removeAccount( email.license )
                  logger.info("*** Emailer removeAccount: {}", email.license)
                } else if ( message.messageId() == email.id ) {
                  store.updateEmail( email.copy(processed = true, valid = true) )
                  logger.info("*** Emailer updateEmail: {}", email)
                } else logger.info("*** Emailer invalid message: {}", message)
              }
            }
          case Failure(error) => logger.info("*** Emailer listEmails failed: {}", error)
        }
        ()
      }.get
  }

  override def receive: Receive = {
    case register: Register => sendEmail(register)
  }
}