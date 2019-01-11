DROP TABLE IF EXISTS owner, pool, surface, pump,
timer, timer_setting, heater, heater_on, heater_off,
cleaning, measurement, chemical, supply, repair;

CREATE TABLE owner (
 license VARCHAR PRIMARY KEY,
 establisehd DATE NOT NULL,
 email VARCHAR NOT NULL
);

CREATE TABLE pool (
  id SERIAL PRIMARY KEY,
  license VARCHAR REFERENCES owner(license),
  built DATE NOT NULL,
  lat DOUBLE PRECISION NOT NULL CHECK (lat > 0.0),
  lon DOUBLE PRECISION NOT NULL CHECK (lon > 0.0),
  volume INTEGER NOT NULL CHECK (volume > 1000)
);

CREATE TABLE surface (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  installed DATE NOT NULL,
  kind VARCHAR NOT NULL
);

CREATE TABLE pump (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer_setting (
  id SERIAL PRIMARY KEY,
  timer_id INTEGER REFERENCES timer(id),
  set DATE NOT NULL,
  set_on TIME(4) NOT NULL,
  set_off TIME(4) NOT NULL CHECK (set_off > set_on)
);

CREATE TABLE heater (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  installed DATE NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE heater_on (
  id SERIAL PRIMARY KEY,
  heater_id INTEGER REFERENCES heater(id),
  temp INTEGER NOT NULL CHECK (temp > 70),
  set DATE NOT NULL
);

CREATE TABLE heater_off (
  id SERIAL PRIMARY KEY,
  heater_id INTEGER REFERENCES heater(id),
  set DATE NOT NULL
);

CREATE TABLE cleaning (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  skimmerBasket BOOL NOT NULL,
  pumpBasket BOOL NOT NULL,
  pumpFilter BOOL NOT NULL,
  pumpChlorineTablets INTEGER NOT NULL,
  deck BOOL NOT NULL
);

CREATE TABLE measurement (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  temp INTEGER CHECK (temp >= 32 AND temp <= 100),
  totalHardness INTEGER NOT NULL CHECK (totalHardness >= 0 AND totalHardness <= 1000),
  totalChlorine INTEGER NOT NULL CHECK (totalChlorine >= 0 AND totalChlorine <= 10),
  totalBromine INTEGER NOT NULL CHECK (totalBromine >= 0 AND totalBromine <= 20),
  freeChlorine INTEGER NOT NULL CHECK (freeChlorine >= 0 AND freeChlorine <= 10),
  ph NUMERIC(1, 1) CHECK (ph >= 6.2 AND ph <= 8.4),
  totalAlkalinity INTEGER NOT NULL CHECK (totalAlkalinity >= 80 AND totalAlkalinity <= 120),
  cyanuricAcid INTEGER NOT NULL CHECK (cyanuricAcid >= 0 AND cyanuricAcid <= 300)
);

CREATE TABLE chemical (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  added DATE NOT NULL,
  chemical VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE supply (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  purchased DATE NOT NULL,
  cost NUMERIC(5, 2) CHECK (cost > 0.0),
  item VARCHAR NOT NULL,
  amount NUMERIC(4, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE repair (
  id SERIAL PRIMARY KEY,
  pool_id INTEGER REFERENCES pool(id),
  repaired DATE NOT NULL,
  cost NUMERIC(5, 2) CHECK (cost > 0.0),
  repair VARCHAR NOT NULL
);