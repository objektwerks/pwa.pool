DROP SCHEMA PUBLIC CASCADE;
CREATE SCHEMA PUBLIC;

CREATE TABLE licensee (
  license VARCHAR PRIMARY KEY,
  email_address VARCHAR NOT NULL,
  created INT NOT NULL,
  activated INT NOT NULL,
  deactivated INT NOT NULL
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
  pool_id INT REFERENCES pool(id),
  installed INT NOT NULL,
  kind VARCHAR NOT NULL
);

CREATE TABLE pump (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE timer_setting (
  id SERIAL PRIMARY KEY,
  timer_id INT REFERENCES timer(id),
  created INT NOT NULL,
  time_on SMALLINT NOT NULL,
  time_off SMALLINT NOT NULL
);

CREATE TABLE heater (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  installed INT NOT NULL,
  model VARCHAR NOT NULL
);

CREATE TABLE heater_setting (
  id SERIAL PRIMARY KEY,
  heater_id INT REFERENCES heater(id),
  temp INT NOT NULL,
  date_on INT NOT NULL,
  date_off INT NOT NULL
);

CREATE TABLE measurement (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  measured INT NOT NULL,
  temp INT NOT NULL,
  total_hardness INT NOT NULL,
  total_chlorine INT NOT NULL,
  total_bromine INT NOT NULL,
  free_chlorine INT NOT NULL,
  ph NUMERIC(2, 1) NOT NULL,
  total_alkalinity INT NOT NULL,
  cyanuric_acid INT NOT NULL
);

CREATE TABLE cleaning (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  cleaned INT NOT NULL,
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  skimmer_basket BOOL NOT NULL,
  pump_basket BOOL NOT NULL,
  pump_filter BOOL NOT NULL,
  deck BOOL NOT NULL
);

CREATE TABLE chemical (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  added INT NOT NULL,
  chemical VARCHAR NOT NULL,
  amount NUMERIC(5, 2),
  unit VARCHAR NOT NULL
);

CREATE TABLE supply (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  purchased INT NOT NULL,
  cost NUMERIC(5, 2) NOT NULL,
  item VARCHAR NOT NULL,
  amount NUMERIC(4, 2) NOT NULL,
  unit VARCHAR NOT NULL
);

CREATE TABLE repair (
  id SERIAL PRIMARY KEY,
  pool_id INT REFERENCES pool(id),
  repaired INT NOT NULL,
  cost NUMERIC(7, 2) NOT NULL,
  repair VARCHAR NOT NULL
);

CREATE TABLE fault (
  cause VARCHAR NOT NULL,
  code INT NOT NULL,
  date_of INT NOT NULL,
  time_of SMALLINT NOT NULL,
  nano_of BIGINT NOT NULL,
  PRIMARY KEY (date_of, time_of, nano_of)
);