Tripletail
----------
>Pool maintenance application.

Scenarios
---------
1. Signup with email.
2. Signin with license and email.
3. List, Add, Edit pool, surface, pump, heater, timer, lifecycle, cleaning, measurement, chemical, supply, repair.
4. Chart measurements, chemicals, supplies and repairs.

Panes
-----
* Signup ( email )
* Signin ( license, email )
* Pools -> Pool -> Surfaces
* Pumps -> Pump
* Timers -> Timer -> TimerSettings
* Heaters -> Heater -> HeaterOn | HeaterOff
* Cleanings | Measurments ** | Chemicals **
* Supplies **
* Repairs **

** Chart included.

Rest
----
* /api/v1/..
* /signup
* /signin
* /list-pools           /add-pool           /update-pool
* /list-surfaces        /add-surface        /update-surface
* /list-pumps           /add-pump           /update-pum
* /list-timers          /add-timer          /update-timer
* /list-timersettings   /add-timersetting   /update-timersetting
* /list-heaters         /add-heater         /update-heater
* /list-heater-ons      /add-heater-on      /update-heater-on
* /list-heater-offs     /add-heater-off     /update-heater-off
* /list-cleanings       /add-cleaning       /update-cleaning
* /list-measurements    /add-measurement    /update-measurement
* /list-chemicals       /add-chemical       /update-chemical
* /list-supplies        /add-supply         /update-supply
* /list-repairs         /add-repair         /update-repair

Object Model
------------
* Licensee(license, email, activated, deactivated)
* Pool(id, license, built, address, volume)
* Surface(id, poolId, installed, kind)
* Pump(id, poolId, installed, model)
* Timer(id, poolId, installed, model)
* TimerSetting(id, timerId, set, setOn, setOff)
* Heater(id, poolId, installed, model)
* HeaterOn(id, heaterId, temp, heaterOn)
* HeaterOff(id, heaterId, heatorOff)
* Cleaning(id, poolId, brush, net, vacuum, skimmerBasket, pumpBasket, pumpFilter, pumpChlorineTablets, deck)
* Measurement(id, poolId, temp, totalHardness, totalChlorine, totalBromine, freeChlorine, ph, totalAlkalinity, cyanuricAcid)
* Chemical(id, poolId, added, chemical, amount, unit)
* Supply(id, poolId, purchased, cost, supply, amount, unit)
* Repair(id, poolId, repaired, cost, repair)

Relational Model
----------------
* Licensee 1 ---> * Pool
* Pool 1 ---> * Surface | Pump | Timer | Heater | Cleaning | Measurement | Chemical | Supply | Repair
* Timer 1 ---> * TimerSetting
* Heater 1 ---> * HeaterOn | HeaterOff

Measurements
------------
1. total hardness 0 - 1000      ok = 250 - 500      ideal = 375
2. total chlorine 0 - 10        ok = 1 - 5          ideal = 3
3. total bromine 0 - 20         ok = 2 - 10         ideal = 5
4. free chlorine 0 - 10         ok = 1 - 5          ideal = 3
5. ph 6.2 - 8.4                 ok = 7.2 - 7.6      ideal = 7.4
6. total alkalinity 0 - 240     ok = 80 - 120       ideal = 100
7. cyanuric acid 0 - 300        ok = 30 - 100       ideal = 50
 
** Units of Measure - oz, gl, lb

Solutions
---------
>Suggested solutions to chemical imbalances.
1. high ph - Sodium Bisulfate
2. low ph - Sodium Carbonate, Soda Ash
3. high alkalinity - Muriatic Acid, Sodium Bisulfate
4. low alkalinity - Sodium Bicarbonate, Baking Soda
5. calcium hardness - Calcium Chloride
6. low chlorine - Chlorine Tablets, Granules, Liquid
7. algae - Algaecide, Shock
8. stains - Stain Identification Kit, Stain Remover

Resources
---------
* descriptions - cleanings, measurements
* images - add, edit, chart

Charts
------
1. measurements - line chart ( x = date, y = chemical ** )
2. chemicals - bar chart ( x = date, y = amount, c = chemical )
3. supplies - bar chart ( x = purchased, y = cost, c = item )
4. repairs - line chart ( x = date, y = cost )