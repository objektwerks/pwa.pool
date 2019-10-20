Tripletail
----------
>Pool maintenance application.

TODO
----
1. Laminar table prototype.

Dev
---
1. sbt
2. clean
3. compile
4. project jvm
5. ~reStart
6. open browser to http://127.0.0.1:7979
7. open developer tools

* View js-fastopt.js in js/target. View sharedjs-fastopt.js in shared/target.
* For step 5, see the build.sbt > mainClass in reStart := Some("tripletail.Server")

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

Build
-----
>View js-fastopt.js in js/target. View sharedjs-fastopt.js in shared/target.
1. sbt clean compile package

Run
---
1. sbt jvm/run
2. open index.html and click target browser in right top corner
3. open developer tools in target brower; select console tab

Panes
-----
1. Account
    * Sign Up ( email )
    * Activate Licensee ( license, email )
    * Sign In ( license, email )
    * Deactivate Licensee ( license, email )
2. Hardware
    * Pools -> Pool
    * Surfaces -> Surface
    * Pumps -> Pump
    * Timers -> Timer -> TimerSettings
    * Heaters -> Heater -> HeaterSettings -> HeaterSetting
3. Maintenance
    * Measurments -> Measurement **
    * Cleanings -> Cleaning **
    * Chemicals -> Chemical **
4. Costs
    * Supplies -> Supply **
    * Repairs -> Repair **
    
** Charts -> measurements, cleanings, chemicals, supplies and repairs

Scenarios
---------
1. If Licensee not saved on client:
    1. Sign In with license and email; or
    2. Sign Up with email
2. If Licensee saved on client, yet not activated:
    1. Activate Licensee with saved license and email
3. If Licensee saved on client and activated:
    1. auto Sign In with license and email
4. List | Add | Update pool, surface, pump, timer, timersetting, heater, heatersetting, cleaning, measurement, chemical, supply, repair
5. If Licensee saved on client and licensee deactivation requested:
    1. Deactivate License with license and email

Rest
----
>Root server url: ""
>Root api url: /api/v1/tripletail
* /signup
* /activatelicensee
* /signin
* /pools           /add   /update
* /surfaces        /add   /update
* /pumps           /add   /update
* /timers          /add   /update
* /timersettings   /add   /update
* /heaters         /add   /update
* /heatersettings  /add   /update
* /measurements    /add   /update
* /cleanings       /add   /update
* /chemicals       /add   /update
* /supplies        /add   /update
* /repairs         /add   /update
* /deactivatelicensee

Client
------
* Entity => Fault | State

Object Model
------------
* Licensee(license, email, created, activated, deactivated)
* Pool(id, license, built, lat, lon, volume)
* Surface(id, poolId, installed, kind)
* Pump(id, poolId, installed, model)
* Timer(id, poolId, installed, model)
* TimerSetting(id, timerId, created, timeOn, timeOff)
* Heater(id, poolId, installed, model)
* HeaterSetting(id, heaterId, temp, dateOn, dateOff)
* Measurement(id, poolId, measured, temp, totalHardness, totalChlorine, totalBromine, freeChlorine, ph, totalAlkalinity, cyanuricAcid)
* Cleaning(id, poolId, cleaned, brush, net, vacuum, skimmerBasket, pumpBasket, pumpFilter, deck)
* Chemical(id, poolId, added, chemical, amount, unit)
* Supply(id, poolId, purchased, cost, supply, amount, unit)
* Repair(id, poolId, repaired, cost, repair)
* Fault(id, message, code, dateOf, timeOf, nanoOf)

Relational Model
----------------
* Licensee 1 ---> * Pool
* Pool 1 ---> * Surface | Pump | Timer | Heater | Measurement | Cleaning | Chemical | Supply | Repair
* Timer 1 ---> * TimerSetting
* Heater 1 ---> * HeaterSetting
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
8. temp 0 - 100

** Units of Measure - oz, gl, lb

Chemicals
---------
1. Chlorine for pool.
2. Chlorine tablets for pool filtration system.

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
1. measurements - line chart ( x = date, y = chemical )
2. cleanings - line chart ( x = date, y = month )
3. chemicals - bar chart ( x = date, y = amount, c = chemical )
4. supplies - bar chart ( x = date, y = cost, c = item )
5. repairs - line chart ( x = date, y = cost )

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
* string - non empty
* string length - lt, lte, eq, gt, gte
* range - inclusive

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