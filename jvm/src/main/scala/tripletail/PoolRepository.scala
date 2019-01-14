package tripletail

import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PoolRepository {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def signup(licensee: Licensee): Future[Unit] = run( query[Licensee]
    .insert( lift(licensee)) )
    .mapTo[Unit]

  def signin(license: String, email: String): Future[Option[Licensee]] = run( query[Licensee]
    .filter( _.license == lift(license) )
    .filter( _.email == lift(email) ) )
    .map(_.headOption)
}