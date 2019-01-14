package tripletail

import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PoolRepository {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def signup(email: String): Future[Licensee] = {
    val licensee = Licensee(email = email)
    val q = quote {
      query[Licensee]
        .insert( lift(licensee) )
        .returning(_.license)
    }
    run(q).map(_ => licensee.copy(license = licensee.license))
  }

  def signin(license: String, email: String): Future[Option[Licensee]] = {
    val q = quote {
      query[Licensee]
        .filter( _.license == lift(license) )
        .filter( _.email == lift(email) )
    }
    run(q).map(_.headOption)
  }
}