package tripletail

import com.typesafe.config.Config

object Emailer {
  def apply(conf: Config): Emailer = new Emailer(conf)
}

class Emailer(conf: Config) {
  def send(address: String): Unit = ()
  def retrieve: Seq[String] = Seq.empty[String]
}