package tripletail

import com.typesafe.config.Config
import io.getquill._

import scala.concurrent.{ExecutionContext, Future}

object Store {
  def apply(conf: Config)(implicit ec: ExecutionContext): Store = new Store(conf)
}

class Store(conf: Config)(implicit ec: ExecutionContext) {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, conf.getConfig("quill.ctx"))
  import ctx._

  def signUp(license: String, emailAddress: String): Future[Licensee] = {
    val licensee = Licensee(license = license, emailAddress = emailAddress)
    ctx.transaction { implicit ec =>
      run(query[Licensee]
        .insert(lift(licensee)))
        .map(_ => licensee)
    }
  }

  def activateLicensee(license: String, emailAddress: String, activatedDate: Int): Future[Option[Licensee]] = {
    ctx.transaction { implicit ec =>
      run( query[Licensee]
        .filter(_.license == lift(license))
        .filter(_.emailAddress == lift(emailAddress))
        .filter(_.activated == 0)
        .filter(_.deactivated == 0)
        .update(_.activated -> lift(activatedDate))
      ).map(_ => Unit)
    }
    getLicensee(license)
  }

  def deactivateLicensee(license: String, emailAddress: String, deactivatedDate: Int): Future[Option[Licensee]] = {
    ctx.transaction { implicit ec =>
      run( query[Licensee]
        .filter(_.license == lift(license))
        .filter(_.emailAddress == lift(emailAddress))
        .filter(_.activated > 0)
        .filter(_.deactivated == 0)
        .update(_.deactivated -> lift(deactivatedDate))
      ).map(_ => Unit)
    }
    getLicensee(license)
  }

  def signIn(license: String, emailAddress: String): Future[Option[Licensee]] =
    run(
      query[Licensee]
        .filter(_.license == lift(license))
        .filter(_.emailAddress == lift(emailAddress))
        .filter(_.activated > 0)
        .filter(_.deactivated == 0)
    ).map(result => result.headOption)

  def getLicensee(license: String): Future[Option[Licensee]] =
    run(
      query[Licensee]
        .filter(_.license == lift(license))
    ).map(result => result.headOption)

  def listPools(license: String): Future[Seq[Pool]] =
    run( query[Pool]
      .filter(_.license == lift(license))
      .sortBy(_.built)(Ord.desc) )

  def addPool(pool: Pool): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pool]
      .insert(lift(pool))
      .returningGenerated(_.id) )
  }

  def updatePool(pool: Pool): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pool]
      .filter(_.id == lift(pool.id))
      .update(lift(pool))
    ).map(_ => 1)
  }

  def listSurfaces(poolId: Int): Future[Seq[Surface]] =
    run( query[Surface]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.installed)(Ord.desc) )

  def addSurface(surface: Surface): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Surface]
      .insert(lift(surface))
      .returningGenerated(_.id) )
  }

  def updateSurface(surface: Surface): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Surface]
      .filter(_.poolId == lift(surface.poolId))
      .update(lift(surface))
    ).map(_ => 1)
  }

  def listPumps(poolId: Int): Future[Seq[Pump]] =
    run( query[Pump]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.installed)(Ord.desc) )

  def addPump(pump: Pump): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pump]
      .insert(lift(pump))
      .returningGenerated(_.id) )
  }

  def updatePump(pump: Pump): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pump]
      .filter(_.poolId == lift(pump.poolId))
      .update(lift(pump))
    ).map(_ => 1)
  }

  def listTimers(poolId: Int): Future[Seq[Timer]] =
    run( query[Timer]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.installed)(Ord.desc) )

  def addTimer(timer: Timer): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Timer]
      .insert(lift(timer))
      .returningGenerated(_.id) )
  }

  def updateTimer(timer: Timer): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Timer]
      .filter(_.poolId == lift(timer.poolId))
      .update(lift(timer))
    ).map(_ => 1)
  }

  def listTimerSettings(timerId: Int): Future[Seq[TimerSetting]] =
    run( query[TimerSetting]
      .filter(_.timerId == lift(timerId))
      .sortBy(_.created)(Ord.desc) )

  def addTimerSetting(timerSetting: TimerSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[TimerSetting]
      .insert(lift(timerSetting))
      .returningGenerated(_.id) )
  }

  def updateTimerSetting(timerSetting: TimerSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[TimerSetting]
      .filter(_.timerId == lift(timerSetting.timerId))
      .update(lift(timerSetting))
    ).map(_ => 1)
  }

  def listHeaters(poolId: Int): Future[Seq[Heater]] =
    run( query[Heater]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.installed)(Ord.desc) )

  def addHeater(heater: Heater): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Heater]
      .insert(lift(heater))
      .returningGenerated(_.id) )
  }

  def updateHeater(heater: Heater): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Heater]
      .filter(_.poolId == lift(heater.poolId))
      .update(lift(heater))
    ).map(_ => 1)
  }

  def listHeaterSettings(heaterId: Int): Future[Seq[HeaterSetting]] =
    run( query[HeaterSetting]
      .filter(_.heaterId == lift(heaterId))
      .sortBy(_.dateOn)(Ord.desc) )

  def addHeaterSetting(heaterSetting: HeaterSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterSetting]
      .insert(lift(heaterSetting))
      .returningGenerated(_.id) )
  }

  def updateHeaterSetting(heaterSetting: HeaterSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterSetting]
      .filter(_.heaterId == lift(heaterSetting.heaterId))
      .update(lift(heaterSetting))
    ).map(_ => 1)
  }

  def listCleanings(poolId: Int): Future[Seq[Cleaning]] =
    run( query[Cleaning]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.cleaned)(Ord.desc) )

  def addCleaning(cleaning: Cleaning): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Cleaning]
      .insert(lift(cleaning))
      .returningGenerated(_.id) )
  }

  def updateCleaning(cleaning: Cleaning): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Cleaning]
      .filter(_.poolId == lift(cleaning.poolId))
      .update(lift(cleaning))
    ).map(_ => 1)
  }

  def listMeasurements(poolId: Int): Future[Seq[Measurement]] =
    run( query[Measurement]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.measured)(Ord.desc) )

  def addMeasurement(measurement: Measurement): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Measurement]
      .insert(lift(measurement))
      .returningGenerated(_.id) )
  }

  def updateMeasurement(measurement: Measurement): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Measurement]
      .filter(_.poolId == lift(measurement.poolId))
      .update(lift(measurement))
    ).map(_ => 1)
  }

  def listChemicals(poolId: Int): Future[Seq[Chemical]] =
    run( query[Chemical]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.added)(Ord.desc) )

  def addChemical(chemical: Chemical): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Chemical]
      .insert(lift(chemical))
      .returningGenerated(_.id) )
  }

  def updateChemical(chemical: Chemical): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Chemical]
      .filter(_.poolId == lift(chemical.poolId))
      .update(lift(chemical))
    ).map(_ => 1)
  }

  def listSupplies(poolId: Int): Future[Seq[Supply]] =
    run( query[Supply]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.purchased)(Ord.desc) )

  def addSupply(supply: Supply): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Supply]
      .insert(lift(supply))
      .returningGenerated(_.id) )
  }

  def updateSupply(supply: Supply): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Supply]
      .filter(_.poolId == lift(supply.poolId))
      .update(lift(supply))
    ).map(_ => 1)
  }

  def listRepairs(poolId: Int): Future[Seq[Repair]] =
    run( query[Repair]
      .filter(_.poolId == lift(poolId))
      .sortBy(_.repaired)(Ord.desc) )

  def addRepair(repair: Repair): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Repair]
      .insert(lift(repair))
      .returningGenerated(_.id) )
  }

  def updateRepair(repair: Repair): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Repair]
      .filter(_.poolId == lift(repair.poolId))
      .update(lift(repair))
    ).map(_ => 1)
  }

  def addFault(fault: Fault): Fault = {
    ctx.transaction { implicit ec =>
      run( query[Fault]
        .insert(lift(fault)) )
    }
    fault
  }
}