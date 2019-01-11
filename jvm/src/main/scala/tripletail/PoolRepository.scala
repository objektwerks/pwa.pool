package tripletail

import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object PoolRepository {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def signup(owner: Owner): Future[Unit] = run( query[Owner].insert(lift(owner)) ).mapTo[Unit]

  def signin(license: String): Future[Option[Owner]] = run( query[Owner].filter(_.license == lift(license)) ).map(_.headOption)
}