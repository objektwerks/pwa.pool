package poolmate

import com.github.blemale.scaffeine.{Cache, Scaffeine}
import com.typesafe.config.Config
import com.typesafe.scalalogging.LazyLogging

import scalikejdbc.*
import scala.concurrent.duration.FiniteDuration

object Store:
  def cache(minSize: Int, maxSize: Int, expireAfter: FiniteDuration): Cache[String, String] =
    Scaffeine()
      .initialCapacity(minSize)
      .maximumSize(maxSize)
      .expireAfterWrite(expireAfter)
      .build[String, String]()

final class Store(conf: Config, cache: Cache[String, String]) extends LazyLogging:
  private val url = conf.getString("db.url")
  private val user = conf.getString("db.user")
  private val password = conf.getString("db.password")
  private val initialSize = conf.getInt("db.initialSize")
  private val maxSize = conf.getInt("db.maxSize")
  private val connectionTimeoutMillis = conf.getLong("db.connectionTimeoutMillis")

  private val settings = ConnectionPoolSettings(
    initialSize = initialSize,
    maxSize = maxSize,
    connectionTimeoutMillis = connectionTimeoutMillis)

  ConnectionPool.singleton(url, user, password, settings)

  def listAccounts(): List[Account] =
    DB readOnly { implicit session =>
      sql"select * from account"
        .map(rs => Account(rs.long("id"), rs.string("license"), rs.string("email_address"), rs.string("pin"), rs.int("activated"), rs.int("deactivated")))
        .list()
    }

  def addAccount(account: Account): Account =
    val id = DB localTx { implicit session =>
      sql"insert into account(license, email_address, pin, activated, deactivated) values(${account.license}, ${account.emailAddress}, ${account.pin}, ${account.activated}, ${account.deactivated})"
      .update()
    }
    account.copy(id = id)

  def removeAccount(license: String): Unit =
    DB localTx { implicit session =>
      sql"delete account where license = $license"
      .update()
    }
    ()

  def listUnprocessedEmails: List[Email] =
    DB readOnly { implicit session =>
      sql"select * from email where processed = false"
        .map(rs => 
          Email(rs.string("id"), rs.string("license"), rs.string("address"), rs.int("date_sent"),
                rs.int("time_sent"), rs.boolean("processed"), rs.boolean("valid")
              )
        )
        .list()
    }

  def addEmail(email: Email): Unit =
    DB localTx { implicit session =>
      sql"""insert into email(id, license, address, date_sent, time_sent, processed, valid)
            values(${email.id}, ${email.license}, ${email.address}, ${email.dateSent},
             ${email.timeSent}, ${email.processed}, ${email.valid})
         """
        .stripMargin
        .update()
    }

  def processEmail(email: Email): Unit =
    DB localTx { implicit session =>
      sql"update email set processed = ${email.processed}, valid = ${email.valid} where id = ${email.id}"
        .update()
    }
    ()
    
  def login(email: String, pin: String): Option[Account] =
    DB readOnly { implicit session =>
      sql"select * from account where email_address = $email and pin = $pin"
        .map(rs => Account(rs.long("id"), rs.string("license"), rs.string("email_address"), rs.string("pin"), rs.int("activated"), rs.int("deactivated")))
        .single()
    }

  def isAuthorized(license: String): Boolean =
    cache.getIfPresent(license) match
      case Some(_) =>
        logger.debug(s"*** store cache get: $license")
        true
      case None =>
        val optionalLicense = DB readOnly { implicit session =>
          sql"select license from account where license = $license"
            .map(rs => rs.string("license"))
            .single()
        }
        if optionalLicense.isDefined then
          cache.put(license, license)
          logger.debug(s"*** store cache put: $license")
          true
        else false

  def deactivate(license: String): Option[Account] =
    DB localTx { implicit session =>
      val deactivated = sql"update account set deactivated = ${DateTime.currentDate}, activated = 0 where license = $license"
      .update()
      if deactivated > 0 then
        sql"select * from account where license = $license"
          .map(rs => Account(rs.long("id"), rs.string("license"), rs.string("email_address"), rs.string("pin"), rs.int("activated"), rs.int("deactivated")))
          .single()
      else None
    }

  def reactivate(license: String): Option[Account] =
    DB localTx { implicit session =>
      val activated = sql"update account set activated = ${DateTime.currentDate}, deactivated = 0 where license = $license"
      .update()
      if activated > 0 then
        sql"select * from account where license = $license"
          .map(rs => Account(rs.long("id"), rs.string("license"), rs.string("email_address"), rs.string("pin"), rs.int("activated"), rs.int("deactivated")))
          .single()
      else None
    }

  def listPools(): List[Pool] =
    DB readOnly { implicit session =>
      sql"select * from pool order by built desc"
        .map(rs => Pool(rs.long("id"), rs.string("license"), rs.string("name"), rs.int("built"), rs.int("volume")))
        .list()
    }

  def addPool(pool: Pool): Pool =
    val id = DB localTx { implicit session =>
      sql"insert into pool(license, name, built, volume) values(${pool.license}, ${pool.name}, ${pool.built}, ${pool.volume})"
      .updateAndReturnGeneratedKey()
    }
    pool.copy(id = id)
    
  def updatePool(pool: Pool): Unit =
    DB localTx { implicit session =>
      sql"update pool set name = ${pool.name}, built = ${pool.built}, volume = ${pool.volume} where id = ${pool.id}"
      .update()
    }
    ()

  def listSurfaces(): List[Surface] =
    DB readOnly { implicit session =>
      sql"select * from surface order by installed desc"
        .map(rs => Surface(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("kind")))
        .list()
    }

  def addSurface(surface: Surface): Surface =
    val id = DB localTx { implicit session =>
      sql"insert into surface(pool_id, installed, kind) values(${surface.poolId}, ${surface.installed}, ${surface.kind})"
      .updateAndReturnGeneratedKey()
    }
    surface.copy(id = id)

  def updateSurface(surface: Surface): Unit =
    DB localTx { implicit session =>
      sql"update surface set installed = ${surface.installed}, kind = ${surface.kind} where id = ${surface.id}"
      .update()
    }
    ()

  def listDecks(): List[Deck] =
    DB readOnly { implicit session =>
      sql"select * from deck order by installed desc"
        .map(rs => Deck(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("kind")))
        .list()
    }

  def addDeck(deck: Deck): Deck =
    val id = DB localTx { implicit session =>
      sql"insert into deck(pool_id, installed, kind) values(${deck.poolId}, ${deck.installed}, ${deck.kind})"
      .updateAndReturnGeneratedKey()
    }
    deck.copy(id = id)

  def updateDeck(deck: Deck): Unit =
    DB localTx { implicit session =>
      sql"update deck set installed = ${deck.installed}, kind = ${deck.kind} where id = ${deck.id}"
      .update()
    }
    ()

  def listPumps(): List[Pump] =
    DB readOnly { implicit session =>
      sql"select * from pump order by installed desc"
        .map(rs => Pump(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("model")))
        .list()
    }

  def addPump(pump: Pump): Pump =
    val id = DB localTx { implicit session =>
      sql"insert into pump(pool_id, installed, model) values(${pump.poolId}, ${pump.installed}, ${pump.model})"
      .updateAndReturnGeneratedKey()
    }
    pump.copy(id = id)  
  
  def updatePump(pump: Pump): Unit =
    DB localTx { implicit session =>
      sql"update pump set installed = ${pump.installed}, model = ${pump.model} where id = ${pump.id}"
      .update()
    }
    ()

  def listTimers(): List[Timer] =
    DB readOnly { implicit session =>
      sql"select * from timer order by installed desc"
        .map(rs => Timer(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("model")))
        .list()
    }

  def addTimer(timer: Timer): Timer =
    val id = DB localTx { implicit session =>
      sql"insert into timer(pool_id, installed, model) values(${timer.poolId}, ${timer.installed}, ${timer.model})"
      .updateAndReturnGeneratedKey()
    }
    timer.copy(id = id)
  
  def updateTimer(timer: Timer): Unit =
    DB localTx { implicit session =>
      sql"update timer set installed = ${timer.installed}, model = ${timer.model} where id = ${timer.id}"
      .update()
    }
    ()

  def listTimerSettings(): List[TimerSetting] =
    DB readOnly { implicit session =>
      sql"select * from timer_setting order by created desc"
        .map(rs => TimerSetting(rs.long("id"), rs.long("timer_id"), rs.int("created"), rs.int("time_on"), rs.int("time_off")))
        .list()
    }

  def addTimerSetting(timerSetting: TimerSetting): TimerSetting =
    val id = DB localTx { implicit session =>
      sql"insert into timer_setting(timer_id, created, time_on, time_off) values(${timerSetting.timerId}, ${timerSetting.created}, ${timerSetting.timeOn}, ${timerSetting.timeOff})"
      .updateAndReturnGeneratedKey()
    }
    timerSetting.copy(id = id)

  def updateTimerSetting(timerSetting: TimerSetting): Unit =
    DB localTx { implicit session =>
      sql"update timer_setting set created = ${timerSetting.created}, time_on = ${timerSetting.timeOn}, time_off = ${timerSetting.timeOff} where id = ${timerSetting.id}"
      .update()
    }
    ()

  def listHeaters(): List[Heater] =
    DB readOnly { implicit session =>
      sql"select * from heater order by installed desc"
        .map(rs => Heater(rs.long("id"), rs.long("pool_id"), rs.int("installed"), rs.string("model")))
        .list()
    }

  def addHeater(heater: Heater): Heater =
    val id = DB localTx { implicit session =>
      sql"insert into heater(pool_id, installed, model) values(${heater.poolId}, ${heater.installed}, ${heater.model})"
      .updateAndReturnGeneratedKey()
    }
    heater.copy(id = id)

  def updateHeater(heater: Heater): Unit =
    DB localTx { implicit session =>
      sql"update heater set installed = ${heater.installed}, model = ${heater.model} where id = ${heater.id}"
      .update()
    }
    ()

  def listHeaterSettings(): List[HeaterSetting] =
    DB readOnly { implicit session =>
      sql"select * from heater_setting order by date_on desc"
        .map(rs => HeaterSetting(rs.long("id"), rs.long("heater_id"), rs.int("temp"), rs.int("date_on"), rs.int("date_off")))
        .list()
    }

  def addHeaterSetting(heaterSetting: HeaterSetting): HeaterSetting =
    val id = DB localTx { implicit session =>
      sql"insert into heater_setting(heater_id, temp, date_on, date_off) values(${heaterSetting.heaterId}, ${heaterSetting.temp}, ${heaterSetting.dateOn}, ${heaterSetting.dateOff})"
      .updateAndReturnGeneratedKey()
    }
    heaterSetting.copy(id = id)

  def updateHeaterSetting(heaterSetting: HeaterSetting): Unit =
    DB localTx { implicit session =>
      sql"update heater_setting set temp = ${heaterSetting.temp}, date_on = ${heaterSetting.dateOn}, date_off = ${heaterSetting.dateOff} where id = ${heaterSetting.id}"
      .update()
    }
    ()

  def listMeasurements(): List[Measurement] =
    DB readOnly { implicit session =>
      sql"select * from measurement order by measured desc"
        .map(rs =>
          Measurement(
            rs.long("id"), rs.long("pool_id"), rs.int("measured"), rs.int("temp"), rs.int("total_hardness"), rs.int("total_chlorine"),
            rs.int("total_bromine"), rs.int("free_chlorine"), rs.float("ph"), rs.int("total_alkalinity"), rs.int("cyanuric_acid")
          )
        )
        .list()
    }

  def addMeasurement(measurement: Measurement): Measurement =
    val id = DB localTx { implicit session =>
      sql"""
        insert into measurement(pool_id, measured, temp, total_hardness, total_chlorine, total_bromine, free_chlorine, ph, total_alkalinity, 
        cyanuric_acid) values(${measurement.poolId}, ${measurement.measured}, ${measurement.temp}, ${measurement.totalHardness},
        ${measurement.totalChlorine}, ${measurement.totalBromine}, ${measurement.freeChlorine}, ${measurement.ph}, ${measurement.totalAlkalinity},
        ${measurement.cyanuricAcid})
        """
        .stripMargin
        .updateAndReturnGeneratedKey()
        
    }
    measurement.copy(id = id)

  def updateMeasurement(measurement: Measurement): Unit =
    DB localTx { implicit session =>
      sql"""
        update measurement set measured = ${measurement.measured}, temp = ${measurement.temp}, total_hardness = ${measurement.totalHardness},
        total_chlorine = ${measurement.totalChlorine}, total_bromine = ${measurement.totalBromine}, free_chlorine = ${measurement.freeChlorine},
        ph = ${measurement.ph}, total_alkalinity = ${measurement.totalAlkalinity}, cyanuric_acid = ${measurement.cyanuricAcid} where id = ${measurement.id}
        """
        .stripMargin
        .update()
    }
    ()
  
  def listCleanings(): List[Cleaning] =
    DB readOnly { implicit session =>
      sql"select * from cleaning order by cleaned desc"
        .map(rs =>
          Cleaning(
            rs.long("id"), rs.long("pool_id"), rs.int("cleaned"), rs.boolean("brush"), rs.boolean("net"), rs.boolean("vacuum"),
            rs.boolean("skimmer_basket"), rs.boolean("pump_basket"), rs.boolean("pump_filter"), rs.boolean("deck")
          )
        )
        .list()
    }

  def addCleaning(cleaning: Cleaning): Cleaning =
    val id = DB localTx { implicit session =>
      sql"""
        insert into cleaning(pool_id, cleaned, brush, net, vacuum, skimmer_basket, pump_basket, pump_filter, deck) values(${cleaning.poolId},
        ${cleaning.cleaned}, ${cleaning.brush}, ${cleaning.net}, ${cleaning.vacuum}, ${cleaning.skimmerBasket}, ${cleaning.pumpBasket},
        ${cleaning.pumpFilter}, ${cleaning.deck})
        """
        .stripMargin
        .updateAndReturnGeneratedKey()
        
    }
    cleaning.copy(id = id)

  def updateCleaning(cleaning: Cleaning): Unit =
    DB localTx { implicit session =>
      sql"""
        update cleaning set cleaned = ${cleaning.cleaned}, brush = ${cleaning.brush}, net = ${cleaning.net}, vacuum = ${cleaning.vacuum},
        skimmer_basket = ${cleaning.skimmerBasket}, pump_basket = ${cleaning.pumpBasket}, pump_filter = ${cleaning.pumpFilter},
        deck = ${cleaning.deck} where id = ${cleaning.id}
        """
        .stripMargin
        .update()
    }
    ()

  def listChemicals(): List[Chemical] =
    DB readOnly { implicit session =>
      sql"select * from chemical order by added desc"
        .map(rs => Chemical(rs.long("id"), rs.long("pool_id"), rs.int("added"), rs.string("chemical"), rs.double("amount"), rs.string("unit")))
        .list()
    }

  def addChemical(chemical: Chemical): Chemical =
    val id = DB localTx { implicit session =>
      sql"insert into chemical(pool_id, added, chemical, amount, unit) values(${chemical.poolId}, ${chemical.added}, ${chemical.chemical}, ${chemical.amount}, ${chemical.unit})"
      .updateAndReturnGeneratedKey()
    }
    chemical.copy(id = id)

  def updateChemical(chemical: Chemical): Unit =
    DB localTx { implicit session =>
      sql"update chemical set added = ${chemical.added}, chemical = ${chemical.chemical}, amount = ${chemical.amount}, unit = ${chemical.unit} where id = ${chemical.id}"
      .update()
    }
    ()

  def listSupplies(): List[Supply] =
    DB readOnly { implicit session =>
      sql"select * from supply order by purchased desc"
        .map(rs => Supply(rs.long("id"), rs.long("pool_id"), rs.int("purchased"), rs.string("item"), rs.double("amount"), rs.string("unit"), rs.double("cost")))
        .list()
    }

  def addSupply(supply: Supply): Supply =
    val id = DB localTx { implicit session =>
      sql"insert into supply(pool_id, purchased, item, amount, unit, cost) values(${supply.poolId}, ${supply.purchased}, ${supply.item}, ${supply.amount}, ${supply.unit}, ${supply.cost})"
      .updateAndReturnGeneratedKey()
    }
    supply.copy(id = id)

  def updateSupply(supply: Supply): Unit =
    DB localTx { implicit session =>
      sql"update supply set purchased = ${supply.purchased}, item = ${supply.item}, amount = ${supply.amount}, unit = ${supply.unit}, cost = ${supply.cost} where id = ${supply.id}"
      .update()
    }
    ()

  def listRepairs(): List[Repair] =
    DB readOnly { implicit session =>
      sql"select * from repair order by repaired desc"
        .map(rs => Repair(rs.long("id"), rs.long("pool_id"), rs.int("repaired"), rs.string("repair"), rs.double("cost")))
        .list()
    }

  def addRepair(repair: Repair): Repair =
    val id = DB localTx { implicit session =>
      sql"insert into repair(pool_id, repaired, repair, cost) values(${repair.poolId}, ${repair.repaired}, ${repair.repair}, ${repair.cost})"
      .updateAndReturnGeneratedKey()
    }
    repair.copy(id = id)

  def updateRepair(repair: Repair): Unit =
    DB localTx { implicit session =>
      sql"update repair set repaired = ${repair.repaired}, repair = ${repair.repair}, cost = ${repair.cost} where id = ${repair.id}"
      .update()
    }
    ()

  def listFaults: List[Fault] =
    DB readOnly { implicit session =>
      sql"select * from fault order by date_of, time_of desc"
        .map(rs => Fault(rs.int("date_of"), rs.int("time_of"), rs.int("nano_of"), rs.string("cause")))
        .list()
    }

  def addFault(fault: Fault): Unit =
    DB localTx { implicit session =>
      sql"insert into fault(date_of, time_of, nano_of, cause) values(${fault.dateOf}, ${fault.timeOf}, ${fault.nanoOf}, ${fault.cause})"
        .update()
    }