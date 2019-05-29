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

  def onFault(fault: Fault): Unit = {
    run( query[Fault].insert(lift(fault)) )
    ()
  }

  def signUp(email: String): Future[Licensee] = {
    val licensee = Licensee(email = email)
    run( query[Licensee].insert(lift(licensee))).map(_ => licensee)
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

  def listPools(license: String): Future[Seq[Pool]] = run( query[Pool].filter(_.license == lift(license)) )

  def addPool(pool: Pool): Future[Int] = run( query[Pool].insert(lift(pool)).returning(_.id) )

  def updatePool(pool: Pool): Future[Int] = run( query[Pool].update(lift(pool)) ).mapTo[Int]

  def listSurfaces(poolId: Int): Future[Seq[Surface]] = run( query[Surface].filter(_.poolId == lift(poolId)) )

  def addSurface(surface: Surface): Future[Int] = run( query[Surface].insert(lift(surface)).returning(_.id) )

  def updateSurface(surface: Surface): Future[Int] = run( query[Surface].update(lift(surface)) ).mapTo[Int]

  def listPumps(poolId: Int): Future[Seq[Pump]] = run( query[Pump].filter(_.id == lift(poolId)) )

  def addPump(pump: Pump): Future[Int] = run( query[Pump].insert(lift(pump)).returning(_.id) )

  def updatePump(pump: Pump): Future[Int] = run( query[Pump].update(lift(pump)) ).mapTo[Int]

  def listTimers(poolId: Int): Future[Seq[Timer]] = run( query[Timer].filter(_.id == lift(poolId)) )

  def addTimer(timer: Timer): Future[Int] = run( query[Timer].insert(lift(timer)).returning(_.id) )

  def updateTimer(timer: Timer): Future[Int] = run( query[Timer].update(lift(timer)) ).mapTo[Int]

  def listTimerSettings(timerId: Int): Future[Seq[TimerSetting]] = run( query[TimerSetting].filter(_.id == lift(timerId)) )

  def addTimerSetting(timerSetting: TimerSetting): Future[Int] = run( query[TimerSetting].insert(lift(timerSetting)).returning(_.id) )

  def updateTimerSetting(timerSetting: TimerSetting): Future[Int] = run( query[TimerSetting].update(lift(timerSetting)) ).mapTo[Int]

  def listHeaters(poolId: Int): Future[Seq[Heater]] = run( query[Heater].filter(_.id == lift(poolId)) )

  def addHeater(heater: Heater): Future[Int] = run( query[Heater].insert(lift(heater)).returning(_.id) )

  def updateHeater(heater: Heater): Future[Int] = run( query[Heater].update(lift(heater)) ).mapTo[Int]

  def listHeaterOns(heaterId: Int): Future[Seq[HeaterOn]] = run( query[HeaterOn].filter(_.id == lift(heaterId)) )

  def addHeaterOn(heaterOn: HeaterOn): Future[Int] = run( query[HeaterOn].insert(lift(heaterOn)).returning(_.id) )

  def updateHeaterOn(heaterOn: HeaterOn): Future[Int] = run( query[HeaterOn].update(lift(heaterOn)) ).mapTo[Int]

  def listHeaterOffs(heaterId: Int): Future[Seq[HeaterOff]] = run( query[HeaterOff].filter(_.id == lift(heaterId)) )

  def addHeaterOff(heaterOff: HeaterOff): Future[Int] = run( query[HeaterOff].insert(lift(heaterOff)).returning(_.id) )

  def updateHeaterOff(heaterOff: HeaterOff): Future[Int] = run( query[HeaterOff].update(lift(heaterOff)) ).mapTo[Int]

  def listCleanings(poolId: Int): Future[Seq[Cleaning]] = run( query[Cleaning].filter(_.id == lift(poolId)) )

  def addCleaning(cleaning: Cleaning): Future[Int] = run( query[Cleaning].insert(lift(cleaning)).returning(_.id) )

  def updateCleaning(cleaning: Cleaning): Future[Int] = run( query[Cleaning].update(lift(cleaning)) ).mapTo[Int]

  def listMeasurements(poolId: Int): Future[Seq[Measurement]] = run( query[Measurement].filter(_.id == lift(poolId)) )

  def addMeasurement(measurement: Measurement): Future[Int] = run( query[Measurement].insert(lift(measurement)).returning(_.id) )

  def updateMeasurement(measurement: Measurement): Future[Int] = run( query[Measurement].update(lift(measurement)) ).mapTo[Int]

  def listChemicals(poolId: Int): Future[Seq[Chemical]] = run( query[Chemical].filter(_.id == lift(poolId)) )

  def addChemical(chemical: Chemical): Future[Int] = run( query[Chemical].insert(lift(chemical)).returning(_.id) )

  def updateChemical(chemical: Chemical): Future[Int] = run( query[Chemical].update(lift(chemical)) ).mapTo[Int]

  def listSupplies(poolId: Int): Future[Seq[Supply]] = run( query[Supply].filter(_.id == lift(poolId)) )

  def addSupply(supply: Supply): Future[Int] = run( query[Supply].insert(lift(supply)).returning(_.id) )

  def updateSupply(supply: Supply): Future[Int] = run( query[Supply].update(lift(supply)) ).mapTo[Int]

  def listRepairs(poolId: Int): Future[Seq[Repair]] = run( query[Repair].filter(_.id == lift(poolId)) )

  def addRepair(repair: Repair): Future[Int] = run( query[Repair].insert(lift(repair)).returning(_.id) )

  def updateRepair(repair: Repair): Future[Int] = run( query[Repair].update(lift(repair)) ).mapTo[Int]
}