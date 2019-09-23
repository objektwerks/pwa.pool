DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

CREATE TABLE licensee (
  license VARCHAR PRIMARY KEY,
  email VARCHAR NOT NULL,
  activated INTEGER NOT NULL,
  deactivated INTEGER NULL
);

CREATE TABLE pool (
  id SERIAL PRIMARY KEY,
  license VARCHAR REFERENCES licensee(license),
  built INTEGER NOT NULL,
  lat NUMERIC(8, 6) NOT NULL CHECK (lat > -89 AND lat < 91),
  lon NUMERIC(9, 6) NOT NULL CHECK (lon > -179 AND lat < 181),
  volume INT NOT NULL CHECK (volume > 1000)
);

CREATE TABLE surface (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INTEGER NOT NULL,
  kind VARCHAR NOT NULL
);

CREATE TABLE pump (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INTEGER NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INTEGER NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timersetting (
  id SERIAL PRIMARY KEY,
  timerId INT REFERENCES timer(id),
  set INTEGER NOT NULL,
  setOn SMALLINT NOT NULL,
  setOff SMALLINT NOT NULL CHECK (setOff > setOn)
);

CREATE TABLE heater (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INTEGER NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE heateron (
  id SERIAL PRIMARY KEY,
  heaterId INT REFERENCES heater(id),
  temp INT NOT NULL CHECK (temp > 70),
  set INTEGER NOT NULL
);

CREATE TABLE heateroff (
  id SERIAL PRIMARY KEY,
  heaterId INT REFERENCES heater(id),
  set INTEGER NOT NULL
);

CREATE TABLE cleaning (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  cleaned INTEGER NOT NULL,
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  skimmerBasket BOOL NOT NULL,
  pumpBasket BOOL NOT NULL,
  pumpFilter BOOL NOT NULL,
  pumpChlorineTablets INT NOT NULL,
  deck BOOL NOT NULL
);

CREATE TABLE measurement (
  id SERIAL PRIMARY KEY,
  measured INTEGER NOT NULL,
  poolId INT REFERENCES pool(id),
  temp INT CHECK (temp >= 32 AND temp <= 100),
  totalHardness INT NOT NULL CHECK (totalHardness >= 0 AND totalHardness <= 1000),
  totalChlorine INT NOT NULL CHECK (totalChlorine >= 0 AND totalChlorine <= 10),
  totalBromine INT NOT NULL CHECK (totalBromine >= 0 AND totalBromine <= 20),
  freeChlorine INT NOT NULL CHECK (freeChlorine >= 0 AND freeChlorine <= 10),
  ph NUMERIC(2, 1) CHECK (ph >= 6.2 AND ph <= 8.4),
  totalAlkalinity INT NOT NULL CHECK (totalAlkalinity >= 80 AND totalAlkalinity <= 120),
  cyanuricAcid INT NOT NULL CHECK (cyanuricAcid >= 0 AND cyanuricAcid <= 300)
);

CREATE TABLE chemical (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  added INTEGER NOT NULL,
  chemical VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE supply (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  purchased INTEGER NOT NULL,
  cost NUMERIC(5, 2) CHECK (cost > 0.0),
  item VARCHAR NOT NULL,
  amount NUMERIC(4, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE repair (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  repaired INTEGER NOT NULL,
  cost NUMERIC(7, 2) CHECK (cost > 0.0),
  repair VARCHAR NOT NULL
);

CREATE TABLE fault (
  message VARCHAR NOT NULL,
  code INT NOT NULL,
  dateOf INTEGER NOT NULL,
  timeOf SMALLINT NOT NULL,
  nanoOf BIGINT NOT NULL,
  PRIMARY KEY (dateOf, timeOf, nanoOf)
);