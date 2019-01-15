package tripletail

import io.getquill._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object Repository {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, "quill.ctx")
  import ctx._

  def signUp(email: String): Future[Licensee] = {
    val licensee = Licensee(email = email)
    run( query[Licensee].insert(lift(licensee)) ).map(_ => licensee)
  }

  def signIn(license: String, email: String): Future[Option[Licensee]] = {
    run(
      query[Licensee]
        .filter( _.license == lift(license) )
        .filter( licensee => licensee.email == lift(email) && licensee.deactivated.isEmpty )
    ).map(_.headOption)
  }

  def listPools(license: String): Future[Seq[Pool]] = run( query[Pool].filter(_.license == lift(license)) )

  def addPool(pool: Pool): Future[Int] = run( query[Pool].insert(lift(pool)).returning(_.id) )

  def updatePool(pool: Pool): Future[Unit] = run( query[Pool].update(lift(pool)) ).mapTo[Unit]

  def listSurfaces(poolId: Int): Future[Seq[Surface]] = run( query[Surface].filter(_.poolId == lift(poolId)) )

  def addSurface(surface: Surface): Future[Int] = run( query[Surface].insert(lift(surface)).returning(_.id) )

  def updateSurface(surface: Surface): Future[Unit] = run( query[Surface].update(lift(surface)) ).mapTo[Unit]
}