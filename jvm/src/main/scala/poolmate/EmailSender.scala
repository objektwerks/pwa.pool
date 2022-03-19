package poolmate

import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import javax.mail.Flags

import jodd.mail.{Email, ImapServer, MailServer, SmtpServer}
import jodd.mail.EmailFilter._

import scala.annotation.tailrec
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.{Failure, Success, Using, Try}

final class EmailSender(conf: Config, store: Store) extends LazyLogging:
  private val host = conf.getString("email.host")
  private val to = conf.getString("email.to")
  private val password = conf.getString("email.password")
  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val salutation = conf.getString("email.salutation")
  private val message = conf.getString("email.message")
  private val closing = conf.getString("email.closing")

  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(host)
    .auth(to, password)
    .buildSmtpMailServer()

  private def buildEmail(account: Account): Email = {
    val html = s"""
                  |<!DOCTYPE html>
                  |<html lang="en">
                  |<head>
                  |<meta charset="utf-8">
                  |<title>$subject</title>
                  |</head>
                  |<body>
                  |<p>$salutation</p>
                  |<p>$message</p>
                  |<p>$closing</p>
                  |</body>
                  |</html>
                  |""".stripMargin
    Email.create()
      .from(from)
      .to(account.emailAddress)
      .subject(subject)
      .htmlMessage(html, "UTF-8")
  }

  @tailrec
  private def retry[T](n: Int)(fn: => T): T =
    Try { fn } match {
      case Success(result) => result
      case _ if n >= 1 => retry(n - 1)(fn)
      case Failure(error) => throw error
    }

  private def sendEmail(register: Register): Either[Throwable, Registered] =
    Using( smtpServer.createSession ) { session =>
      session.open()

      var account = Account(emailAddress = register.emailAddress)
      val messageId = session.sendMail(buildEmail(account))
      logger.info("*** EmailSender sent message id: {}", messageId)

      account = store.addAccount(account)
      logger.info("*** EmailSender added account: {}", account)

      val email = poolmate.Email(messageId, account.license, account.emailAddress)
      store.addEmail(email)
      logger.info("*** EmailSender added email: {}", email)
      
      Registered(account)
    }.toEither

  def send(register: Register): Either[Throwable, Registered] =
    retry[Either[Throwable, Registered]](1)(sendEmail(register))