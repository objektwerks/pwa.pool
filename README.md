Tripletail
----------
>Pool maintenance application.

Scenarios
---------
1. Signup with email.
2. Signin with license.
3. Add, Edit pool, surface, pump, heater, timer, lifecycle, cleaning, measurement, chemical, supply, repair.
4. View measurements, chemicals, supplies and repairs chart.

Panes
-----
* Signup
* Signin
* Owner
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
* /owners/:license
* /owners/:license/pools
* /pool/:id/surfaces
* /pool/:id/pumps
* /pool/:id/timers
* /pool/:id/timers/:pid/timersettings
* /pool/:id/heaters/:pid/heaterons
* /pool/:id/heaters/:pid/heateroffs
* /pool/:id/heaters
* /pool/:id/cleanings
* /pool/:id/measurements
* /pool/:id/chemicals
* /pool/:id/supplies
* /pool/:id/repairs

Object Model
------------
* Owner(license, email)
* Pool(id, license, built, lat, lon, volume)
* Surface(id, pid, installed, kind)
* Pump(id, pid, installed, model)
* Timer(id, pid, installed, model)
* TimerSetting(id, tid, set, on, off)
* Heater(id, pid, installed, model)
* HeaterOn(id, hid, temp, on)
* HeaterOff(id, hid, off)
* Cleaning(id, pid, brush, net, vacuum, skimmerBasket, pumpBasket, pumpFilter, pumpChlorineTablets, deck)
* Measurement(id, pid, temp, totalHardness, totalChlorine, totalBromine, freeChlorine, ph, totalAlkalinity, cyanuricAcid)
* Chemical(id, pid, added, name, amount, unit)
* Supply(id, pid, purchased, cost, name, amount, unit)
* Repair(id, pid, repaired, cost, repair)

Relational Model
----------------
* Owner 1 ---> * Pool
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