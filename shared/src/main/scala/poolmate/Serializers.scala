package poolmate

object Serializers:
  import upickle.default.*

  given accountRW: ReadWriter[Account] = macroRW
  given poolRW: ReadWriter[Pool] = macroRW
  given surfaceRW: ReadWriter[Surface] = macroRW
  given deckRW: ReadWriter[Deck] = macroRW
  given pumpRW: ReadWriter[Pump] = macroRW
  given timerRW: ReadWriter[Timer] = macroRW
  given timerSettingRW: ReadWriter[TimerSetting] = macroRW
  given heaterRW: ReadWriter[Heater] = macroRW
  given heaterSettingRW: ReadWriter[HeaterSetting] = macroRW
  given measurementRW: ReadWriter[Measurement] = macroRW
  given cleaningRW: ReadWriter[Cleaning] = macroRW
  given chemicalRW: ReadWriter[Chemical] = macroRW
  given supplyRW: ReadWriter[Supply] = macroRW
  given repairRW: ReadWriter[Repair] = macroRW

  given entityRW: ReadWriter[Entity] = ReadWriter.merge(
    accountRW, poolRW, surfaceRW, deckRW, pumpRW, timerRW, timerSettingRW, heaterRW,
    heaterSettingRW, measurementRW, cleaningRW, chemicalRW, supplyRW, repairRW
  )

  given registerRW: ReadWriter[Register] = macroRW
  given loginRW: ReadWriter[Login] = macroRW

  given deactivateRW: ReadWriter[Deactivate] = macroRW
  given reactivateRW: ReadWriter[Reactivate] = macroRW

  given listPoolsRW: ReadWriter[ListPools] = macroRW
  given addPoolRW: ReadWriter[AddPool] = macroRW
  given updatePoolRW: ReadWriter[UpdatePool] = macroRW

  given listSurfacesRW: ReadWriter[ListSurfaces] = macroRW
  given addSurfaceRW: ReadWriter[AddSurface] = macroRW
  given updateSurfaceRW: ReadWriter[UpdateSurface] = macroRW

  given listPumpsRW: ReadWriter[ListPumps] = macroRW
  given addPumpRW: ReadWriter[AddPump] = macroRW
  given updatePumpRW: ReadWriter[UpdatePump] = macroRW

  given listTimersRW: ReadWriter[ListTimers] = macroRW
  given addTimerRW: ReadWriter[AddTimer] = macroRW
  given updateTimerRW: ReadWriter[UpdateTimer] = macroRW

  given listTimerSettingsRW: ReadWriter[ListTimerSettings] = macroRW
  given addTimerSettingRW: ReadWriter[AddTimerSetting] = macroRW
  given updateTimerSettingRW: ReadWriter[UpdateTimerSetting] = macroRW

  given listHeatersRW: ReadWriter[ListHeaters] = macroRW
  given addHeaterRW: ReadWriter[AddHeater] = macroRW
  given updateHeaterRW: ReadWriter[UpdateHeater] = macroRW

  given listHeaterSettingsRW: ReadWriter[ListHeaterSettings] = macroRW
  given addHeaterSettingRW: ReadWriter[AddHeaterSetting] = macroRW
  given updateHeaterSettingRW: ReadWriter[UpdateHeaterSetting] = macroRW

  given listMeasurementsRW: ReadWriter[ListMeasurements] = macroRW
  given addMeasurementRW: ReadWriter[AddMeasurement] = macroRW
  given updateMeasurementRW: ReadWriter[UpdateMeasurement] = macroRW

  given listCleaningsRW: ReadWriter[ListCleanings] = macroRW
  given addCleaningRW: ReadWriter[AddCleaning] = macroRW
  given updateCleaningRW: ReadWriter[UpdateCleaning] = macroRW

  given listChemicalsRW: ReadWriter[ListChemicals] = macroRW
  given addChemicalRW: ReadWriter[AddChemical] = macroRW
  given updateChemicalRW: ReadWriter[UpdateChemical] = macroRW

  given listSuppliesRW: ReadWriter[ListSupplies] = macroRW
  given addSupplyRW: ReadWriter[AddSupply] = macroRW
  given updateSupplyRW: ReadWriter[UpdateSupply] = macroRW

  given listRepairsRW: ReadWriter[ListRepairs] = macroRW
  given addRepairRW: ReadWriter[AddRepair] = macroRW
  given updateRepairRW: ReadWriter[UpdateRepair] = macroRW

  given commandRW: ReadWriter[Command] = ReadWriter.merge(
    registerRW, loginRW, deactivateRW, reactivateRW, listPoolsRW, addPoolRW,
    updatePoolRW, listSurfacesRW, addSurfaceRW, updateSurfaceRW, listPumpsRW,
    addPumpRW, updatePumpRW, listTimersRW, addTimerRW, updateTimerRW,
    listTimerSettingsRW, addTimerSettingRW, updateTimerSettingRW, listHeatersRW,
    addHeaterRW, updateHeaterRW, listHeaterSettingsRW, addHeaterSettingRW,
    updateHeaterSettingRW, listMeasurementsRW, addMeasurementRW, updateMeasurementRW,
    listCleaningsRW, addCleaningRW, updateCleaningRW, listChemicalsRW, addChemicalRW,
    updateChemicalRW, listSuppliesRW, addSupplyRW, updateSupplyRW, listRepairsRW,
    addRepairRW, updateRepairRW
  )

  given registeredRW: ReadWriter[Registered] = macroRW
  given loggedInRW: ReadWriter[LoggedIn] = macroRW

  given deactivatedRW: ReadWriter[Deactivated] = macroRW
  given reactivatedRW: ReadWriter[Reactivated] = macroRW

  given updatedRW: ReadWriter[Updated] = macroRW

  given poolsListedRW: ReadWriter[PoolsListed] = macroRW
  given poolAddedRW: ReadWriter[PoolAdded] = macroRW

  given surfacesListedRW: ReadWriter[SurfacesListed] = macroRW
  given surfaceAddedRW: ReadWriter[SurfaceAdded] = macroRW

  given pumpsListedRW: ReadWriter[PumpsListed] = macroRW
  given pumpAddedRW: ReadWriter[PumpAdded] = macroRW

  given timersListedRW: ReadWriter[TimersListed] = macroRW
  given timerAddedRW: ReadWriter[TimerAdded] = macroRW

  given timerSettingsListedRW: ReadWriter[TimerSettingsListed] = macroRW
  given timerSettingAddedRW: ReadWriter[TimerSettingAdded] = macroRW

  given heatersListedRW: ReadWriter[HeatersListed] = macroRW
  given heaterAddedRW: ReadWriter[HeaterAdded] = macroRW

  given heaterSettingsListedRW: ReadWriter[HeaterSettingsListed] = macroRW
  given heaterSettingAddedRW: ReadWriter[HeaterSettingAdded] = macroRW

  given measurementsListedRW: ReadWriter[MeasurementsListed] = macroRW
  given measurementAddedRW: ReadWriter[MeasurementAdded] = macroRW

  given cleaningsListedRW: ReadWriter[CleaningsListed] = macroRW
  given cleaningAddedRW: ReadWriter[CleaningAdded] = macroRW

  given chemicalsListedRW: ReadWriter[ChemicalsListed] = macroRW
  given chemicalAddedRW: ReadWriter[ChemicalAdded] = macroRW

  given suppliesListedRW: ReadWriter[SuppliesListed] = macroRW
  given supplyAddedRW: ReadWriter[SupplyAdded] = macroRW

  given repairsListedRW: ReadWriter[RepairsListed] = macroRW
  given repairAddedRW: ReadWriter[RepairAdded] = macroRW

  given faultRW: ReadWriter[Fault] = macroRW

  given eventRW: ReadWriter[Event] = ReadWriter.merge(
    registeredRW, loggedInRW, deactivatedRW, reactivatedRW, updatedRW, faultRW,
    poolsListedRW, poolAddedRW, surfacesListedRW, surfaceAddedRW, pumpsListedRW,
    pumpAddedRW, timersListedRW, timerAddedRW, timerSettingsListedRW,
    timerSettingAddedRW, heatersListedRW, heaterAddedRW, heaterSettingsListedRW,
    heaterSettingAddedRW, measurementsListedRW, measurementAddedRW, cleaningsListedRW,
    cleaningAddedRW, chemicalsListedRW, chemicalAddedRW, suppliesListedRW,
    supplyAddedRW, repairsListedRW, repairAddedRW
  )