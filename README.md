Tripletail
----------
>Pool maintenance application.

Postgresql
----------
1. /usr/local/var/postgres/postgresql.conf : listen_addresses = ‘*’, port = 5432
2. in build.sbt : javaOptions in IntegrationTest += "-Dquill.binds.log=true"
3. brew services start postgresql | brew services stop postgresql
4. /usr/local/var/log/postgres.log : verify database is running or is shutdown

DDL
---
1. psql tripletail
2. \i ddl.sql
3. \q

Quill
-----
>To eliminate unused ExecutionContext variable errors, this scalac option must be set in the build.sbt:
```
scalacOptions ++= Seq(
  "-Ywarn-macros:after"
)
```
>Quill macros use the implicit ExecutionContext. The Quill ctx.transaction code does not.

Dev
---
1. sbt
2. project jvm
3. ~reStart
4. open browser to http://127.0.0.1:7979

Test
----
1. sbt clean test | sbt clean it:test

Bloop
-----
1. sbt bloopInstall
2. bloop clean tripletail
3. bloop compile tripletail
4. bloop test sharedJVM
5. bloop test jvm-it

Run
---
1. sbt jvm/run
2. open browser to http://127.0.0.1:7979

Scenarios
---------
1. If Licensee not persisted on client:
    1. Sign up with email; or
    2. Sign in with license and email
2. Else If Licensee is persisted on client:
    1. Sign in with persisted Licensee(license and emails)
3. List | Add | Update pool, surface, pump, timer, timersetting heater, heater-on, heater-off, lifecycle, cleaning, measurement, chemical, supply, repair
4. Chart measurements, chemicals, supplies and repairs

Panes
-----
* Sign Up ( email ) | Sign In ( license, email )
* Pools -> Pool
* Surfaces -> Surface
* Pumps -> Pump
* Timers -> Timer -> TimerSettings
* Heaters -> Heater -> HeaterOn | HeaterOff
* Cleanings -> Cleaning
* Measurments -> Measurement **
* Chemicals -> Chemical **
* Supplies -> Supply **
* Repairs -> Repair **

** Chart included.

Rest
----
* /api/v1/..
* /signup
* /signin
* /pools           /add   /update
* /surfaces        /add   /update
* /pumps           /add   /update
* /timers          /add   /update
* /timersettings   /add   /update
* /heaters         /add   /update
* /heater-ons      /add   /update
* /heater-offs     /add   /update
* /cleanings       /add   /update
* /measurements    /add   /update
* /chemicals       /add   /update
* /supplies        /add   /update
* /repairs         /add   /update

Object Model
------------
* Licensee(license, email, activated, deactivated)
* Pool(id, license, built, lat, lon, volume)
* Surface(id, poolId, installed, kind)
* Pump(id, poolId, installed, model)
* Timer(id, poolId, installed, model)
* TimerSetting(id, timerId, set, setOn, setOff)
* Heater(id, poolId, installed, model)
* HeaterOn(id, heaterId, temp, heaterOn)
* HeaterOff(id, heaterId, heatorOff)
* Cleaning(id, poolId, cleaned, brush, net, vacuum, skimmerBasket, pumpBasket, pumpFilter, pumpChlorineTablets, deck)
* Measurement(id, poolId, measured, temp, totalHardness, totalChlorine, totalBromine, freeChlorine, ph, totalAlkalinity, cyanuricAcid)
* Chemical(id, poolId, added, chemical, amount, unit)
* Supply(id, poolId, purchased, cost, supply, amount, unit)
* Repair(id, poolId, repaired, cost, repair)
* Fault(id, message, code, occured, at)

Relational Model
----------------
* Licensee 1 ---> * Pool
* Pool 1 ---> * Surface | Pump | Timer | Heater | Cleaning | Measurement | Chemical | Supply | Repair
* Timer 1 ---> * TimerSetting
* Heater 1 ---> * HeaterOn | HeaterOff
* Fault

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

Date
----
1. Format: yyyy-MM-dd
2. String: 1999-03-03, 1999-12-13
3. Int: 19990303, 19991213

Time
----
1. Format: HH:mm
2. String: 03:03, 23:33
3. Int: 303, 2333

Validation
----------
* string - not null, non empty
* string length - less than, equal, greater than
* email - is valid via regex
* number - less than, equal, greater than, range
* date - is valid
* time - is valid