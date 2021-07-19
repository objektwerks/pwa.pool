package pool

import com.typesafe.config.Config

import jodd.mail.{Email, MailServer, SmtpServer}

import scala.concurrent.Future
import scala.util.{Try, Using}

object Emailer {
  def apply(conf: Config): Emailer = new Emailer(conf)
}

final class Emailer(conf: Config) {
  private val smtpServer: SmtpServer = MailServer.create()
    .ssl(true)
    .host(conf.getString("email.smtp.host"))
    .auth(conf.getString("email.smtp.user"), conf.getString("email.smtp.password"))
    .buildSmtpMailServer()

  private val from = conf.getString("email.from")
  private val subject = conf.getString("email.subject")
  private val message = conf.getString("email.message")
  private val email = conf.getString("email.email")
  private val lic = conf.getString("email.lic")
  private val pin = conf.getString("email.pin")
  private val instructions = conf.getString("email.instructions")

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

  def sendEmail(account: Account): Future[Try[String]] = {
    Future.successful(
      Using( smtpServer.createSession ) { session =>
        session.open()
        session.sendMail( buildEmail(account) )
      }
    )
  }
}