package tripletail

import io.getquill.{PostgresAsyncContext, SnakeCase}

object PoolRepository {
  lazy val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
}
