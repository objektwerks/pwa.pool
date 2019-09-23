DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

CREATE TABLE licensee (
  license VARCHAR PRIMARY KEY,
  email VARCHAR NOT NULL,
  activated INT NOT NULL,
  deactivated INT NULL
);

CREATE TABLE pool (
  id SERIAL PRIMARY KEY,
  license VARCHAR REFERENCES licensee(license),
  built INT NOT NULL,
  lat NUMERIC(8, 6) NOT NULL,
  lon NUMERIC(9, 6) NOT NULL,
  volume INT NOT NULL
);

CREATE TABLE surface (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INT NOT NULL,
  kind VARCHAR NOT NULL
);

CREATE TABLE pump (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timersetting (
  id SERIAL PRIMARY KEY,
  timerId INT REFERENCES timer(id),
  set INT NOT NULL,
  setOn SMALLINT NOT NULL,
  setOff SMALLINT NOT NULL
);

CREATE TABLE heater (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE heateron (
  id SERIAL PRIMARY KEY,
  heaterId INT REFERENCES heater(id),
  temp INT NOT NULL,
  setOn INT NOT NULL
);

CREATE TABLE heateroff (
  id SERIAL PRIMARY KEY,
  heaterId INT REFERENCES heater(id),
  setOf INT NOT NULL
);

CREATE TABLE cleaning (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  cleaned INT NOT NULL,
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
  measured INT NOT NULL,
  poolId INT REFERENCES pool(id),
  temp INT NOT NULL,
  totalHardness INT NOT NULL,
  totalChlorine INT NOT NULL,
  totalBromine INT NOT NULL,
  freeChlorine INT NOT NULL,
  ph NUMERIC(2, 1) NOT NULL,
  totalAlkalinity INT NOT NULL,
  cyanuricAcid INT NOT NULL
);

CREATE TABLE chemical (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  added INT NOT NULL,
  chemical VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE supply (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  purchased INT NOT NULL,
  cost NUMERIC(5, 2) NOT NULL,
  item VARCHAR NOT NULL,
  amount NUMERIC(4, 2) NOT NULL,
  unit VARCHAR NOT NULL
);

CREATE TABLE repair (
  id SERIAL PRIMARY KEY,
  poolId INT REFERENCES pool(id),
  repaired INT NOT NULL,
  cost NUMERIC(7, 2) NOT NULL,
  repair VARCHAR NOT NULL
);

CREATE TABLE fault (
  message VARCHAR NOT NULL,
  code INT NOT NULL,
  dateOf INT NOT NULL,
  timeOf SMALLINT NOT NULL,
  nanoOf BIGINT NOT NULL,
  PRIMARY KEY (dateOf, timeOf, nanoOf)
);