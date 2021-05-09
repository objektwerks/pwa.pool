Scalajs Pool App
----------------
>Pool maintenance PWA app using Akka-Http, Quil, UPickle, ScalaJs and Postgresql.

Install
-------
1. brew install postgresql
2. brew install node
3. npm install jsdom

Requirements
------------
1. run on JDK 8. See .sbtopts
2. provide valid Postgresql configuration in (1) server.conf and (2) test.server.conf
3. provide valid SMTP configuration in (1) server.conf and (2) test.server.conf

Bundling
--------
>None of the following Scalajs bundling options yield satisfactory results:
1. ScalaJS Bundler: https://scalacenter.github.io/scalajs-bundler/index.html
2. Sbt Web: https://github.com/sbt/sbt-web
3. Sbt Web ScalaJS: https://github.com/vmunier/sbt-web-scalajs
>There is ***no*** Scalajs bundling standard.

jsEnv
-----
>Using ( libraryDependencies += "org.scala-js" %% "scalajs-env-jsdom-nodejs" % "1.1.0" ) in plugins.sbt
>provides access to a NodeJs environment ( github.com/scala-js/scala-js-env-jsdom-nodejs ). Yet
>the following jsEnv shortcomings exist:
1. NodeJs - Window object not supported.
2. NodeJs and Jsdom - Window object supported. IndexedDB not supported. Other Windows libraries likely not supported.
3. PhantomJS - Throws exception. Advanced configuration not available.
4. Selenium - Doesn't support headless.
>Use utest ( www.lihaoyi.com/post/uTesttheEssentialTestFrameworkforScala.html ) for testing.

Dev
---
>[ shared ]
1. sbt [ interactive session ]
2. project shared
3. clean | compile | test
4. ~compile
>[ jvm ]
1. sbt [ interactive session ]
2. project jvm
3. clean | compile | test | it:test
4. ~reStart
>[ js ]
1. sbt [ interactive session ]
2. project js
3. clean | compile
4. ~fastLinkJS | ~fullLinkJS
5. open target/scala-2.13/classes/public/index.html and click target browser in right top corner ( Intellij )
6. open developer tools

Test
----
1. sbt clean sharedJVM/test
2. sbt clean jvm/it:test
3. sbt clean js/test ( no test(s) at this time )

Run
---
>jvm
1. sbt jvm/run
2. curl -v http://localhost:7979/now
>js
1. In Intellij, open js/target/scala-2.13/classes/public/index.html and click target browser in right top corner
2. **Or** in VsCode, right click js/target/scala-2.13/classes/public/index.html and open with Live Server ( must install )
3. open browser developer tools

Package
-------
>See www.scala-sbt.org/sbt-native-packager/formats/universal.html for details on
>universal:packageZipTarball. See jvm | js/target/universal for output.

>jvm
1. sbt jvm/universal:packageZipTarball
>js
1. sbt js/fullLinkJS
2. sbt js/universal:packageZipTarball

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
1. on osx intel: /usr/local/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
2. on osx m1: /opt/homebrew/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
3. brew services start postgresql
4. on osx intel: /usr/local/var/log/postgres.log
5. on m1: /opt/homebrew/var/log/postgres.log
6. in build.sbt : IntegrationTest / javaOptions += "-Dquill.binds.log=true"

Database
--------
>Example database url: postgresql://localhost:5432/pool?user=mycomputername&password='"
1. psql postgres
2. CREATE DATABASE pool OWNER <my computer name>;
3. GRANT ALL PRIVILEGES ON DATABASE pool TO <my computer name>;
4. \l
5. \q
6. psql pool
7. \i ddl.sql
8. \q

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