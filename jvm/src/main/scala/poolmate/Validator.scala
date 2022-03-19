package poolmate

import Validators.*

final class Validator():
  def isValid(command: Command): Boolean =
    command match
      case register: Register => register.isValid
      case login: Login => login.isValid

      case deactivate: Deactivate => deactivate.isValid
      case reactivate: Reactivate => reactivate.isValid

      case list: ListPools => true
      case add: AddPool => add.pool.isValid
      case update: UpdatePool => update.pool.isValid

      case list: ListSurfaces => true
      case add: AddSurface => add.surface.isValid
      case update: UpdateSurface => update.surface.isValid

      case list: ListDecks => true
      case add: AddDeck => add.deck.isValid
      case update: UpdateDeck => update.deck.isValid

      case list: ListPumps => true
      case add: AddPump => add.pump.isValid
      case update: UpdatePump => update.pump.isValid

      case list: ListTimers => true
      case add: AddTimer => add.timer.isValid
      case update: UpdateTimer => update.timer.isValid

      case list: ListTimerSettings => true
      case add: AddTimerSetting => add.timerSetting.isValid
      case update: UpdateTimerSetting => update.timerSetting.isValid

      case list: ListHeaters => true
      case add: AddHeater => add.heater.isValid
      case update: UpdateHeater => update.heater.isValid

      case list: ListHeaterSettings => true
      case add: AddHeaterSetting => add.heaterSetting.isValid
      case update: UpdateHeaterSetting => update.heaterSetting.isValid

      case list: ListMeasurements => true
      case add: AddMeasurement => add.measurement.isValid
      case update: UpdateMeasurement => update.measurement.isValid

      case list: ListCleanings => true
      case add: AddCleaning => add.cleaning.isValid
      case update: UpdateCleaning => update.cleaning.isValid

      case list: ListChemicals => true
      case add: AddChemical => add.chemical.isValid
      case update: UpdateChemical => update.chemical.isValid

      case list: ListSupplies => true
      case add: AddSupply => add.supply.isValid
      case update: UpdateSupply => update.supply.isValid

      case list: ListRepairs => true
      case add: AddRepair => add.repair.isValid
      case update: UpdateRepair => update.repair.isValid