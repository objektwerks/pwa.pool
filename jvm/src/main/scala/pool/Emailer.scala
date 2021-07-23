package pool

import akka.actor.Actor

import com.typesafe.config.Config

import jodd.mail.{Email, ImapServer, MailServer, SmtpServer}

import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Using

final case class SendEmail(account: Account) extends Product with Serializable

final class Emailer(conf: Config) extends Actor {
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

  private def sendEmail(account: Account): String =
    Using( smtpServer.createSession ) { session =>
      session.open()
      session.sendMail( buildEmail(account) )
    }.getOrElse("Message-ID not provided.")

  private def receiveEmail(): Runnable = new Runnable() {
    override def run(): Unit =
      Using( imapServer.createSession ) { session =>
        session.open()
        // Todo - process new account emails
        ()
      }.get
  }

  override def receive: Receive = {
    case send: SendEmail => sender() ! sendEmail(send.account)
  }
}