package tripletail

import io.getquill._
import scala.concurrent.{ExecutionContext, Future}

object PoolRepository {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def getOwner(license: String)(implicit ec: ExecutionContext): Future[Option[Owner]] = {
    run( query[Owner].filter(_.license == lift(license)) ).map(_.headOption)
  }
}