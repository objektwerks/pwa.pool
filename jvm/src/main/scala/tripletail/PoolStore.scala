package tripletail

import com.typesafe.config.Config
import io.getquill._

import scala.concurrent.{ExecutionContext, Future}

object PoolStore {
  def apply(conf: Config)(implicit ec: ExecutionContext): PoolStore = new PoolStore(conf)
}

class PoolStore(conf: Config)(implicit ec: ExecutionContext) {
  implicit val ctx = new PostgresAsyncContext(SnakeCase, conf.getConfig("quill.ctx"))
  import ctx._

  def signUp(email: String): Future[Licensee] = {
    val licensee = Licensee(email = email)
    ctx.transaction { implicit ec =>
      run(query[Licensee].insert(lift(licensee))).map(_ => licensee)
    }
  }

  def signIn(license: String, email: String): Future[Option[Licensee]] = {
    run(
      query[Licensee]
        .filter( licensee => licensee.license == lift(license) )
        .filter( licensee => licensee.email == lift(email) && licensee.deactivated.isEmpty )
    ).map(result => result.headOption)
  }

  def getLicensee(license: String): Future[Option[Licensee]] = {
    run(
      query[Licensee]
        .filter( licensee => licensee.license == lift(license) )
    ).map(result => result.headOption)
  }

  def listPools(license: String): Future[Seq[Pool]] =
    run( query[Pool].filter(_.license == lift(license)).sortBy(p => p.built)(Ord.desc) )

  def addPool(pool: Pool): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pool].insert(lift(pool)).returning(_.id) )
  }

  def updatePool(pool: Pool): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pool].filter(_.id == lift(pool.id)).update(lift(pool)) ).map(_ => 1)
  }

  def listSurfaces(poolId: Int): Future[Seq[Surface]] = run( query[Surface].filter(_.poolId == lift(poolId)) )

  def addSurface(surface: Surface): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Surface].insert(lift(surface)).returning(_.id) )
  }

  def updateSurface(surface: Surface): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Surface].filter(_.poolId == lift(surface.poolId)).update(lift(surface)) ).map(_ => 1)
  }

  def listPumps(poolId: Int): Future[Seq[Pump]] = run( query[Pump].filter(_.id == lift(poolId)) )

  def addPump(pump: Pump): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pump].insert(lift(pump)).returning(_.id) )
  }

  def updatePump(pump: Pump): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Pump].filter(_.poolId == lift(pump.poolId)).update(lift(pump)) ).map(_ => 1)
  }

  def listTimers(poolId: Int): Future[Seq[Timer]] = run( query[Timer].filter(_.id == lift(poolId)) )

  def addTimer(timer: Timer): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Timer].insert(lift(timer)).returning(_.id) )
  }

  def updateTimer(timer: Timer): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Timer].filter(_.poolId == lift(timer.poolId)).update(lift(timer)) ).map(_ => 1)
  }

  def listTimerSettings(timerId: Int): Future[Seq[TimerSetting]] = run( query[TimerSetting].filter(_.id == lift(timerId)) )

  def addTimerSetting(timerSetting: TimerSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[TimerSetting].insert(lift(timerSetting)).returning(_.id) )
  }

  def updateTimerSetting(timerSetting: TimerSetting): Future[Int] = ctx.transaction { implicit ec =>
    run( query[TimerSetting].filter(_.timerId == lift(timerSetting.timerId)).update(lift(timerSetting)) ).map(_ => 1)
  }

  def listHeaters(poolId: Int): Future[Seq[Heater]] = run( query[Heater].filter(_.id == lift(poolId)) )

  def addHeater(heater: Heater): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Heater].insert(lift(heater)).returning(_.id) )
  }

  def updateHeater(heater: Heater): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Heater].filter(_.poolId == lift(heater.poolId)).update(lift(heater)) ).map(_ => 1)
  }

  def listHeaterOns(heaterId: Int): Future[Seq[HeaterOn]] = run( query[HeaterOn].filter(_.id == lift(heaterId)) )

  def addHeaterOn(heaterOn: HeaterOn): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterOn].insert(lift(heaterOn)).returning(_.id) )
  }

  def updateHeaterOn(heaterOn: HeaterOn): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterOn].filter(_.heaterId == lift(heaterOn.heaterId)).update(lift(heaterOn)) ).map(_ => 1)
  }

  def listHeaterOffs(heaterId: Int): Future[Seq[HeaterOff]] = run( query[HeaterOff].filter(_.id == lift(heaterId)) )

  def addHeaterOff(heaterOff: HeaterOff): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterOff].insert(lift(heaterOff)).returning(_.id) )
  }

  def updateHeaterOff(heaterOff: HeaterOff): Future[Int] = ctx.transaction { implicit ec =>
    run( query[HeaterOff].filter(_.heaterId == lift(heaterOff.heaterId)).update(lift(heaterOff)) ).map(_ => 1)
  }

  def listCleanings(poolId: Int): Future[Seq[Cleaning]] = run( query[Cleaning].filter(_.id == lift(poolId)) )

  def addCleaning(cleaning: Cleaning): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Cleaning].insert(lift(cleaning)).returning(_.id) )
  }

  def updateCleaning(cleaning: Cleaning): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Cleaning].filter(_.poolId == lift(cleaning.poolId)).update(lift(cleaning)) ).map(_ => 1)
  }

  def listMeasurements(poolId: Int): Future[Seq[Measurement]] = run( query[Measurement].filter(_.id == lift(poolId)) )

  def addMeasurement(measurement: Measurement): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Measurement].insert(lift(measurement)).returning(_.id) )
  }

  def updateMeasurement(measurement: Measurement): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Measurement].filter(_.poolId == lift(measurement.poolId)).update(lift(measurement)) ).map(_ => 1)
  }

  def listChemicals(poolId: Int): Future[Seq[Chemical]] = run( query[Chemical].filter(_.id == lift(poolId)) )

  def addChemical(chemical: Chemical): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Chemical].insert(lift(chemical)).returning(_.id) )
  }

  def updateChemical(chemical: Chemical): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Chemical].filter(_.poolId == lift(chemical.poolId)).update(lift(chemical)) ).map(_ => 1)
  }

  def listSupplies(poolId: Int): Future[Seq[Supply]] = run( query[Supply].filter(_.id == lift(poolId)) )

  def addSupply(supply: Supply): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Supply].insert(lift(supply)).returning(_.id) )
  }

  def updateSupply(supply: Supply): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Supply].filter(_.poolId == lift(supply.poolId)).update(lift(supply)) ).map(_ => 1)
  }

  def listRepairs(poolId: Int): Future[Seq[Repair]] = run( query[Repair].filter(_.id == lift(poolId)) )

  def addRepair(repair: Repair): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Repair].insert(lift(repair)).returning(_.id) )
  }

  def updateRepair(repair: Repair): Future[Int] = ctx.transaction { implicit ec =>
    run( query[Repair].filter(_.poolId == lift(repair.poolId)).update(lift(repair)) ).map(_ => 1)
  }

  def addFault(fault: Fault): Unit = {
    ctx.transaction { implicit ec => run( query[Fault].insert(lift(fault)) ) }
    ()
  }
}