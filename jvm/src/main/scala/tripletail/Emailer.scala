package tripletail

import akka.actor.{Actor, ActorLogging}
import com.typesafe.config.Config
import jodd.mail.{Email, MailServer, SendMailSession, SmtpServer}

import scala.util.control.NonFatal

class Emailer(conf: Config) extends Actor with ActorLogging {
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
  private val activating = conf.getString("email.activating")
  private val activated = conf.getString("email.activated")
  private val activationFailed = conf.getString("email.activationFailed")
  private val retries = conf.getInt("email.retries")

  private def buildEmail(to: String, license: String, uri: String): Email = {
    val html = s"""
                  |<!DOCTYPE html>
                  |<html lang="en">
                  |<head>
                  |<meta charset="utf-8">
                  |<title>$subject</title>
                  |</head>
                  |<body>
                  |<h3>$subject</h3>
                  |<div>
                  |<p>$email $to</p>
                  |<p>$lic $license</p>
                  |<p id="onActivated"></p>
                  |<p>$message<button type="button" onclick="activateLicense()">$subject</button></p>
                  |</div>
                  |<script>
                  |function activateLicense() {
                  |  var xhr = new XMLHttpRequest();
                  |  xhr.onreadystatechange = function() {
                  |    document.getElementById("onActivated").innerHTML = $activating
                  |    if (this.readyState == 4 && this.status == 200) {
                  |      document.getElementById("onActivated").innerHTML = $activated + " on " + xhr.responseText;
                  |    } else if (this.readyState == 4 && this.status != 200) {
                  |      document.getElementById("onActivated").innerHTML = $activationFailed
                  |    }
                  |  };
                  |  xhr.open("GET", https://$uri/activatelicense/$license, false);
                  |}
                  |</script>
                  |</body>
                  |</html>
                  |""".stripMargin
    Email.create()
      .from(from)
      .to(to)
      .subject(subject)
      .htmlMessage(html, "UTF-8")
  }

  private def sendEmail(to: String, license: String, uri: String): Option[String] = {
    var session: SendMailSession = null
    var messageId: Option[String] = None
    try {
      session = smtpServer.createSession
      session.open()
      messageId = Some( session.sendMail(buildEmail(to, license, uri)) )
    } catch {
      case NonFatal(error) => log.error(s"*** Emailer send to: $to failed: ${error.getMessage}")
    } finally {
      session.close()
    }
    messageId
  }

  override def receive: Receive = {
    case send: SendEmail =>
      var attempts = 0
      var messageId: Option[String] = None
      while ( attempts < retries && messageId.isEmpty ) {
        messageId = sendEmail(send.to, send.license, send.uri)
        attempts = attempts + 1
      }
  }
}