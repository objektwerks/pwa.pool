package tripletail

import io.getquill.{PostgresAsyncContext, SnakeCase}
import tripletail.Entity._

import scala.concurrent.{ExecutionContext, Future}

object PoolRepository {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def getOwner(id: Integer)(implicit ec: ExecutionContext): Future[Owner] =
    for {
      q <- ctx.run(query[Owner].filter(o => o.id == lift(id)))
      r <- q
    } yield r
}