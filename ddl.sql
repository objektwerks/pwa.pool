DROP TABLE IF EXISTS owners, pools, surfaces, pumps,
timers, timersettings, heaters, heateron, heateroff,
cleanings, measurements, chemicals, supplies, repairs;

CREATE TABLE owners (
 id SERIAL PRIMARY KEY,
 email VARCHAR NOT NULL,
 establisehd TIMESTAMP NOT NULL,
 license VARCHAR NOT NULL
);

CREATE TABLE pools (
  id SERIAL PRIMARY KEY,
  owner_id INTEGER REFERENCES owners(id),
  built DATE NOT NULL,
  lat DOUBLE PRECISION NOT NULL CHECK (lat > 0.0),
  lon DOUBLE PRECISION NOT NULL CHECK (lon > 0.0),
  volume INTEGER NOT NULL CHECK (volume > 1000)
);

CREATE TABLE surfaces (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  installed DATE NOT NULL,
  kind VARCHAR NOT NULL
);

CREATE TABLE pumps (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timers (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timersettings (
  id SERIAL PRIMARY KEY,
  timer_id INTEGER REFERENCES timers(id),
  set DATE NOT NULL,
  timer_on TIME(4) NOT NULL,
  timer_off TIME(4) NOT NULL CHECK (timer_on > timer_off)
);

CREATE TABLE heaters (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE heateron (
  id SERIAL PRIMARY KEY,
  heater_id INTEGER REFERENCES heaters(id),
  temp INTEGER NOT NULL CHECK (temp > 70),
  heater_on DATE NOT NULL
);

CREATE TABLE heateroff (
  id SERIAL PRIMARY KEY,
  heater_id INTEGER REFERENCES heaters(id),
  heater_off DATE NOT NULL
);

CREATE TABLE cleanings (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  skimmerBasket BOOL NOT NULL,
  pumpBasket BOOL NOT NULL,
  pumpFilter BOOL NOT NULL,
  pumpChlorineTablets INTEGER NOT NULL CHECK (pumpChlorineTablets > 0),
  deck BOOL NOT NULL
);

CREATE TABLE measurements (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  temp INTEGER CHECK (temp >= 32 AND temp <= 100),
  totalHardness INTEGER NOT NULL CHECK (totalHardness >= 0 AND totalHardness <= 1000),
  totalChlorine INTEGER NOT NULL CHECK (totalChlorine >= 0 AND totalChlorine <= 10),
  totalBromine INTEGER NOT NULL CHECK (totalBromine >= 0 AND totalBromine <= 20),
  freeChlorine INTEGER NOT NULL CHECK (freeChlorine >= 0 AND freeChlorine <= 10),
  ph NUMERIC(1, 1) CHECK (ph >= 6.2 AND ph <= 8.4),
  totalAlkalinity INTEGER NOT NULL CHECK (totalAlkalinity >= 80 AND totalAlkalinity <= 120),
  cyanuricAcid INTEGER NOT NULL CHECK (cyanuricAcid >= 0 AND cyanuricAcid <= 300)
);

CREATE TABLE chemicals (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  added DATE NOT NULL,
  chemical VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE supplies (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  purchased DATE NOT NULL,
  cost NUMERIC(5, 2) CHECK (cost > 0.0),
  item VARCHAR NOT NULL,
  amount NUMERIC(4, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE repairs (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pools(id),
  repaired DATE NOT NULL,
  cost NUMERIC(5, 2) CHECK (cost > 0.0),
  repair VARCHAR NOT NULL
);