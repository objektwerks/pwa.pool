PWA Pool App
------------
>PWA Pool maintenance app using Akka-Http, Quill, uPickle, ScalaJs, Laminar and Postgresql.

Install
-------
1. brew install postgresql
2. brew install node
3. npm install jsdom

Configuration
-------------
>Provide valid configuration for:
1. Postgresql
2. SMTP
>in these conf files:
1. jvm/src/it/resources/test.server.conf
2. jvm/src/main/resoures/server.conf

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
>provides access to a NodeJs environment ( github.com/scala-js/scala-js-env-jsdom-nodejs ) for testing.
>Yet the following jsEnv shortcomings exist:
1. NodeJs - Window object not supported.
2. NodeJs and Jsdom - Window object supported. IndexedDB not supported. Other Windows libraries likely not supported.
3. PhantomJS - Throws exception. Advanced configuration not available.
4. Selenium - Doesn't support headless.
>Use utest ( www.lihaoyi.com/post/uTesttheEssentialTestFrameworkforScala.html ) for testing.

Dev
---
>[ shared ]
1. sbt [ interactive session ]
2. project shared/clean | compile | test
>[ jvm ]
1. sbt [ interactive session ]
2. project jvm/clean | compile | test | it:test | run
>[ js ]
1. sbt [ interactive session ]
2. project js/clean | fastLinkJS | fullLinkJS
3. open target/scala-2.13/classes/public/index.html and:
    1. via **Intellij**, click target browser in right top corner **OR**
    2. via **VSCode**, right click open with Live Server ( VSCode ) ( must install Live Server extension )
4. open developer tools in index.html browser tab

Test
----
1. sbt sharedJVM/clean test
2. sbt jvm/clean it:test

Run
---
>jvm
1. sbt jvm/run  [ curl -v http://localhost:7979/now ]
>js
1. open target/scala-2.13/classes/public/index.html and:
   1. via **Intellij**, click target browser in right top corner **OR**
   2. via **VSCode**, right click open with Live Server ( VSCode ) ( must install Live Server extension )
2. open developer tools in index.html browser tab

Package
-------
>See sbt-native-packager ( www.scala-sbt.org/sbt-native-packager/formats/universal.html ) for
>details on universal:packageZipTarball.

>jvm
1. sbt jvm/universal:packageZipTarball
>js
1. sbt js/clean fullLinkJS
2. sbt js/universal:packageZipTarball

Http Codes
----------
>The Router only emits: 200, 400, 401, 500

Client
------
* Command => Fault | Event
* Entity  => Fault | State

Proxy
-----
* CommandProxy => EventHandler
* EntityProxy => StateHandler

Account
-------
* Register( email ) => Registering( inProgress )
* Login( email, pin ) => LoggedIn( account )
* Deactivate( license ) => Deactivated( account )
* Reactivate( license ) => Reactivated( account )

Dialogs
-------
* Register ( email )
* Login ( email, pin )
* Account ( license, email, pin, activated, deactivated )
* Pool ( pool )

Views
-----
1. Pool
   * Pools -> Pool
   * Surfaces -> Surface
   * Pumps -> Pump
   * Timers -> Timer -> TimerSettings
   * Heaters -> Heater -> HeaterSettings -> HeaterSetting
2. Maintenance
   * Measurments -> Measurement **
   * Cleanings -> Cleaning **
   * Chemicals -> Chemical **
3. Expenses
   * Supplies -> Supply **
   * Repairs -> Repair **

** Charts -> measurements, cleanings, chemicals, supplies and repairs.

Rest
----
>Public url: /
* /now
* /register
* /login
* /deactivate
* /reactivate
>API url: /api/v1/pool 
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

Object Model
------------
* License(key)
* Account(license, email, pin, activated, deactivated)
* Pool(id, license, name, built, volume)
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
* Email(id, license, address, processed, valid)
* Fault(id, dateOf, timeOf, code, cause)

Relational Model
----------------
* License
* Account 1 ---> * Pool
* Pool 1 ---> * Surface | Pump | Timer | Heater | Measurement | Cleaning | Chemical | Supply | Repair
* Timer 1 ---> * TimerSetting
* Heater 1 ---> * HeaterSetting
* Email
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
3. Pool Shock

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
2. String: 1999-01-01, 1999-12-16
3. Int: 19990101, 19991216

Time
----
1. Format: HH:mm
2. String: 01:01, 19:14
3. Int: 101, 1914

Postgresql
----------
1. conf:
    1. on osx intel: /usr/local/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
    2. on osx m1: /opt/homebrew/var/postgres/postgresql.conf : listen_addresses = ‘localhost’, port = 5432
2. build.sbt:
    1. IntegrationTest / javaOptions += "-Dquill.binds.log=true"
3. run:
    1. brew services start postgresql
4. logs:
    1. on osx intel: /usr/local/var/log/postgres.log
    2. on m1: /opt/homebrew/var/log/postgres.log

Database
--------
>Example database url: postgresql://localhost:5432/pool?user=mycomputername&password='"
1. psql postgres
2. CREATE DATABASE pool OWNER [your computer name];
3. GRANT ALL PRIVILEGES ON DATABASE pool TO [your computer name];
4. \l
5. \q
6. psql pool
7. \i ddl.sql
8. \q

DDL
---
>Alternatively run: psql -d pool -f ddl.sql ( see RouterTest )
1. psql pool
2. \i ddl.sql
3. \q

Drop
----
1. psql postgres
2. drop database pool;
3. \q

Config
------
>See these files:
1. jvm/src/it/resources/test.server.conf
2. jvm/src/main/resoures/server.conf

Quill
-----
>To eliminate unused ExecutionContext variable errors, this scalac option must be set in the build.sbt:
```
scalacOptions ++= Seq(
  "-Ywarn-macros:after"
)
```
>Quill macros use the implicit ExecutionContext. The Quill ctx.transaction code does not.
