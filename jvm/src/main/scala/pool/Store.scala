package pool

import com.typesafe.config.Config

import io.getquill._

import scala.concurrent.{ExecutionContext, Future}

object Store {
  def apply(conf: Config)(implicit ec: ExecutionContext): Store = new Store(conf)
}

class Store(conf: Config)(implicit ec: ExecutionContext) {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, conf.getConfig("quill.ctx"))
  import ctx._

  def listEmails: Future[Seq[Email]] =
    run(
      query[Email]
        .filter(_.processed == lift(false))
    )

  def addEmail(email: Email): Future[Unit] =
    transaction { implicit ec =>
      run(
        query[Email]
          .insert(lift(email))
      ).map(_ => ())
    }

  def updateEmail(email: Email): Future[Unit] =
    transaction { implicit ec =>
      run(
        query[Email]
          .filter(_.id == lift(email.id))
          .update(lift(email))
      ).map(_ => ())
    }

  def registerAccount(account: Account): Future[Unit] =
    transaction { implicit ec =>
      run(
        query[Account]
          .insert(lift(account))
      ).map(_ => ())
    }

  def loginAccount(email: String, pin: String): Future[Option[Account]] =
    run(
      query[Account]
        .filter(_.email == lift(email))
        .filter(_.pin == lift(pin))
    ).map(result => result.headOption)

  def deactivateAccount(license: String): Future[Option[Account]] = {
    transaction { implicit ec =>
      run(
        query[Account]
          .filter(_.license == lift(license))
          .update(_.deactivated -> lift(DateTime.currentDate), _.activated -> lift(0))
      )
    }
    getAccount(license)
  }

  def reactivateAccount(license: String): Future[Option[Account]] = {
    transaction { implicit ec =>
      run(
        query[Account]
          .filter(_.license == lift(license))
          .update(_.activated -> lift(DateTime.currentDate), _.deactivated -> lift(0))
      )
    }
    getAccount(license)
  }

  def listAccounts: Future[Seq[Account]] =
    run(
      query[Account]
        .sortBy(_.activated)(Ord.desc)
    )

  def getAccount(license: String): Future[Option[Account]] =
    run(
      query[Account]
        .filter(_.license == lift(license))
    ).map(result => result.headOption)

  def removeAccount(license: String): Future[Unit] =
    transaction { implicit ec =>
      run(
        query[Account]
          .filter(_.license == lift(license))
          .delete
      ).map(_ => ())
    }

  def listPools(license: String): Future[Seq[Pool]] =
    run(
      query[Pool]
        .filter(_.license == lift(license))
        .sortBy(_.built)(Ord.desc)
    )

  def addPool(pool: Pool): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Pool]
          .insert(lift(pool))
          .returningGenerated(_.id)
      )
    }

  def updatePool(pool: Pool): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Pool]
          .filter(_.id == lift(pool.id))
          .update(lift(pool))
          .returning(_ => 1)
      )
    }

  def listSurfaces(poolId: Int): Future[Seq[Surface]] =
    run(
      query[Surface]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.installed)(Ord.desc)
    )

  def addSurface(surface: Surface): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Surface]
          .insert(lift(surface))
          .returningGenerated(_.id)
      )
    }

  def updateSurface(surface: Surface): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Surface]
          .filter(_.poolId == lift(surface.poolId))
          .update(lift(surface))
          .returning(_ => 1)
      )
    }

  def listPumps(poolId: Int): Future[Seq[Pump]] =
    run(
      query[Pump]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.installed)(Ord.desc)
    )

  def addPump(pump: Pump): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Pump]
          .insert(lift(pump))
          .returningGenerated(_.id)
      )
    }

  def updatePump(pump: Pump): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Pump]
          .filter(_.poolId == lift(pump.poolId))
          .update(lift(pump))
          .returning(_ => 1)
      )
    }

  def listTimers(poolId: Int): Future[Seq[Timer]] =
    run(
      query[Timer]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.installed)(Ord.desc)
    )

  def addTimer(timer: Timer): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Timer]
          .insert(lift(timer))
          .returningGenerated(_.id)
      )
    }

  def updateTimer(timer: Timer): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Timer]
          .filter(_.poolId == lift(timer.poolId))
          .update(lift(timer))
          .returning(_ => 1)
      )
    }

  def listTimerSettings(timerId: Int): Future[Seq[TimerSetting]] =
    run(
      query[TimerSetting]
        .filter(_.timerId == lift(timerId))
        .sortBy(_.created)(Ord.desc)
    )

  def addTimerSetting(timerSetting: TimerSetting): Future[Int] =
    transaction { implicit ec =>
      run(
        query[TimerSetting]
          .insert(lift(timerSetting))
          .returningGenerated(_.id)
      )
    }

  def updateTimerSetting(timerSetting: TimerSetting): Future[Int] =
    transaction { implicit ec =>
      run(
        query[TimerSetting]
          .filter(_.timerId == lift(timerSetting.timerId))
          .update(lift(timerSetting))
          .returning(_ => 1)
      )
    }

  def listHeaters(poolId: Int): Future[Seq[Heater]] =
    run(
      query[Heater]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.installed)(Ord.desc)
    )

  def addHeater(heater: Heater): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Heater]
          .insert(lift(heater))
          .returningGenerated(_.id)
      )
    }

  def updateHeater(heater: Heater): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Heater]
          .filter(_.poolId == lift(heater.poolId))
          .update(lift(heater))
          .returning(_ => 1)
      )
    }

  def listHeaterSettings(heaterId: Int): Future[Seq[HeaterSetting]] =
    run(
      query[HeaterSetting]
        .filter(_.heaterId == lift(heaterId))
        .sortBy(_.dateOn)(Ord.desc)
    )

  def addHeaterSetting(heaterSetting: HeaterSetting): Future[Int] =
    transaction { implicit ec =>
      run(
        query[HeaterSetting]
          .insert(lift(heaterSetting))
          .returningGenerated(_.id)
      )
    }

  def updateHeaterSetting(heaterSetting: HeaterSetting): Future[Int] =
    transaction { implicit ec =>
      run(
        query[HeaterSetting]
          .filter(_.heaterId == lift(heaterSetting.heaterId))
          .update(lift(heaterSetting))
          .returning(_ => 1)
      )
    }

  def listCleanings(poolId: Int): Future[Seq[Cleaning]] =
    run(
      query[Cleaning]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.cleaned)(Ord.desc)
    )

  def addCleaning(cleaning: Cleaning): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Cleaning]
          .insert(lift(cleaning))
          .returningGenerated(_.id)
      )
    }

  def updateCleaning(cleaning: Cleaning): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Cleaning]
          .filter(_.poolId == lift(cleaning.poolId))
          .update(lift(cleaning))
          .returning(_ => 1)
      )
    }

  def listMeasurements(poolId: Int): Future[Seq[Measurement]] =
    run(
      query[Measurement]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.measured)(Ord.desc)
    )

  def addMeasurement(measurement: Measurement): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Measurement]
          .insert(lift(measurement))
          .returningGenerated(_.id)
      )
    }

  def updateMeasurement(measurement: Measurement): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Measurement]
          .filter(_.poolId == lift(measurement.poolId))
          .update(lift(measurement))
          .returning(_ => 1)
      )
    }

  def listChemicals(poolId: Int): Future[Seq[Chemical]] =
    run(
      query[Chemical]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.added)(Ord.desc)
    )

  def addChemical(chemical: Chemical): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Chemical]
          .insert(lift(chemical))
          .returningGenerated(_.id)
      )
    }

  def updateChemical(chemical: Chemical): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Chemical]
          .filter(_.poolId == lift(chemical.poolId))
          .update(lift(chemical))
          .returning(_ => 1)
      )
    }

  def listSupplies(poolId: Int): Future[Seq[Supply]] =
    run(
      query[Supply]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.purchased)(Ord.desc)
    )

  def addSupply(supply: Supply): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Supply]
          .insert(lift(supply))
          .returningGenerated(_.id)
      )
    }

  def updateSupply(supply: Supply): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Supply]
          .filter(_.poolId == lift(supply.poolId))
          .update(lift(supply))
          .returning(_ => 1)
      )
    }

  def listRepairs(poolId: Int): Future[Seq[Repair]] =
    run(
      query[Repair]
        .filter(_.poolId == lift(poolId))
        .sortBy(_.repaired)(Ord.desc)
    )

  def addRepair(repair: Repair): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Repair]
          .insert(lift(repair))
          .returningGenerated(_.id)
      )
    }

  def updateRepair(repair: Repair): Future[Int] =
    transaction { implicit ec =>
      run(
        query[Repair]
          .filter(_.poolId == lift(repair.poolId))
          .update(lift(repair))
          .returning(_ => 1)
      )
    }

  def addFault(fault: Fault): Fault = {
    transaction { implicit ec =>
      run(
        query[Fault]
          .insert(lift(fault))
      )
    }
    fault
  }
}