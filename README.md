Scalajs Pool App
----------------
>Pool maintenance PWA app using Akka-Http, Quil, UPickle, ScalaJs and Postgresql.

Install
-------
1. brew install npm
2. npm install jsdom

Note
----
>Must:
1. run on JDK 8. See .sbtopts
2. provide valid Postgresql conf in server.conf and test.server.conf
3. provide valid SMTP conf in server.conf and test.server.conf

ScalaJS Bundling
----------------
>None of these options yield satisfactory results. Currently this project uses the SbtWeb
>plugin and fastOptJS | fullOptJS, with artifacts landing in the js/target/web directory.
1. ScalaJS Bundler: https://scalacenter.github.io/scalajs-bundler/index.html
2. Sbt Web: https://github.com/sbt/sbt-web
3. Sbt Web ScalaJS: https://github.com/vmunier/sbt-web-scalajs

jsEnv
-----
>Using ( libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0" ) in plugins.sbt
>provides access to a NodeJs environment ( https://github.com/scala-js/scala-js-env-jsdom-nodejs ). Yet
>items 1 and 2 below highlight glaring weaknesses.
1. NodeJs - Window object not supported.
2. NodeJs and Jsdom - Window object supported. IndexedDB not supported. Other Windows libraries likely not supported.
3. PhantomJS - Throws exception. Advanced configuration not available.
4. Selenium - Doesn't support headless.

Dev
---
1. sbt clean test
2. sbt js/fastLinkJS | fullLinkJS
>In a unique session [ server ]:
1. sbt [ interactive session ]
2. project jvm
3. ~reStart
>In a unique session [ client ]
1. sbt [ interactive session ]
2. project js
3. ~fastLinkJS | fullLinkJS
4. open js/src/main/assets/index.html and click target browser in right top corner
5. open developer tools

Test
----
1. sbt clean sharedJVM/test
2. sbt clean jvm/it:test
3. sbt clean js/test

Build
-----
1. sbt clean compile

Run
---
1. sbt jvm/run
2. open js/src/main/assets/index.html and click target browser in right top corner
3. open developer tools in target brower; select console tab

Package
-------
1. sbt clean compile
2. sbt jvm/stage | jvm/universal:packageZipTarball
3. sbt js/fastLinkJS | js/fullLinkJS
4. sbt js/webStage | js/universal:packageZipTarball

Panes
-----
>All panes, less Account panes, will include list, add and update features.
1. Account
    * Sign Up ( emailAddress )
    * Activate Licensee ( license, emailAddress )
    * Sign In ( license, emailAddress )
    * Deactivate Licensee ( license, emailAddress )
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

Licensee Scenarios
------------------
1. If Licensee not saved on client, user must:
    * SignIn(license, emailAddress) => SignedIn(Licensee) |
    * SignUp(emailAddress) => SignedUp(Licensee)
2. If Licensee saved on client, yet not activated, user must:
    * ActivateLicensee(license, emailAddress) => LicenseeActivated(Licensee)
3. If Licensee saved on client and activated, user must:
    * SignIn(license, emailAddress) => SignedIn(Licensee)
4. If Licensee saved on client and licensee deactivation selected, user must:
    * DeactivateLicense(license, emailAddress) => LicenseeDeactivated(Licensee)

Alternate Licensee Scenarios
----------------------------
* SignUp(Licensee) => SignedUp(Licensee)
* ActivateLicensee(Licensee) => LicenseeActivated(Licensee)
* SignIn(Licensee) => SignedIn(Licensee)
* DeativateLicensee(Licensee) => LicenseeDeactivated(Licensee)

Rest
----
>Root server url: ""
>Root api url: /api/v1/pool
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
* Command => Fault | Event
* Entity => Fault | State

Object Model
------------
* Licensee(license, emailAddress, created, activated, deactivated)
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
1. brew install postgresql
2. /usr/local/var/postgres/postgresql.conf : listen_addresses = ‘*’, port = 5432
3. brew services start postgresql
4. in build.sbt : javaOptions in IntegrationTest += "-Dquill.binds.log=true"
5. /usr/local/var/log/postgres.log : verify database is running or is shutdown

Database
--------
1. psql postgres
2. CREATE USER tripletail WITH ENCRYPTED PASSWORD 'pool';
3. CREATE DATABASE pool OWNER pool;
4. GRANT ALL PRIVILEGES ON DATABASE pool TO pool;
5. \l
6. \q
7. psql pool
8. \i ddl.sql
9. \q

DDL
---
1. psql pool
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database pool;
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