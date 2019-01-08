DROP TABLE IF EXISTS owners, pools, surfaces, pumps,
timers, timersettings, heaters, heateron, heateroff,
cleanings, measurements, chemicals, supplies, repairs;

CREATE TABLE owners (
 license String DEFAULT uuid_generate_v4(),
 email VARCHAR NOT NULL,
 PRIMARY KEY (license)
);

CREATE TABLE pools {
  id SERIAL NOT NULL,
  license String NOT NULL,
  built DATE NOT NULL,
  lat FLOAT NOT NULL CHECK (lat > 0.0),
  lon FLOAT NOT NULL CHECK (long > 0.0),
  volume INTEGER NOT NULL CHECK (volume > 1000),
  PRIMARY KEY (id, license),
  FOREIGN KEY (license) REFERENCES owners(license)
};

CREATE TABLE surfaces {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  installed DATE NOT NULL,
  kind VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE pumps {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  installed DATE NOT NULL,
  model VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE timers {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  installed DATE NOT NULL,
  model VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE timersettings {
  id SERIAL NOT NULL,
  tid INTEGER NOT NULL,
  set DATE NOT NULL,
  on TIME NOT NULL,
  off TIME NOT NULL CHECK (on > off),
  PRIMARY KEY (id, tid),
  FOREIGN KEY (tid) REFERENCES timers(id)
};

CREATE TABLE heaters {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  installed DATE NOT NULL,
  model VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE heateron {
  id SERIAL NOT NULL,
  hid INTEGER NOT NULL,
  temp INTEGER NOT NULL CHECK (temp > 70),
  on DATE NOT NULL,
  PRIMARY KEY (id, hid),
  FOREIGN KEY (hid) REFERENCES heaters(hid)    
};

CREATE TABLE heateroff {
  id SERIAL NOT NULL,
  hid INTEGER NOT NULL,
  off DATE NOT NULL,
  PRIMARY KEY (id, hid),
  FOREIGN KEY (hid) REFERENCES heaters(hid)    
};

CREATE TABLE cleanings {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  brush BOOL NOT NULL,
  net BOOL NOT NULL,
  vacuum BOOL NOT NULL,
  skimmerBasket BOOL NOT NULL,
  pumpBasket BOOL NOT NULL,
  pumpFilter BOOL NOT NULL,
  pumpChlorineTablets INTEGER NOT NULL CHECK (pumpChlorineTablets > 0),
  deck BOOL NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE measurements {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  temp FLOAT NOT NULL CHECK (temp > 32.0 AND temp < 100.0),
  totalHardness INTEGER NOT NULL CHECK (totalHardness >= 0 AND totalHarness <= 1000),
  totalChlorine INTEGER NOT NULL CHECK (totalChlorine >= 0 AND totalChlorine <= 10),
  totalBromine INTEGER NOT NULL CHECK (totalBromine >= 0 AND totalBromine <= 20),
  freeChlorine INTEGER NOT NULL CHECK (freeChlorine >= 0 AND freeChlorine <= 10),
  ph FLOAT NOT NULL CHECK (ph >= 6.2 AND ph <= 8.4),
  totalAlkalinity INTEGER NOT NULL CHECK (totalAlkalinity >= 80 AND totalAlkalinity <= 120),
  cyanuricAcid INTEGER NOT NULL CHECK (cyanuricAcid >= 0 AND cyanuricAcid <= 300),
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE chemicals {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  added DATE NOT NULL,
  name VACHAR NOT NULL,
  amount FLOAT NOT NULL,
  unit VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE supplies {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  purchased DATE NOT NULL,
  cost FLOAT NOT NULL CHECK (cost > 0.0),
  name VACHAR NOT NULL,
  amount FLOAT NOT NULL,
  unit VARCHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};

CREATE TABLE repairs {
  id SERIAL NOT NULL,
  pid INTEGER NOT NULL,
  repaired DATE NOT NULL,
  cost FLOAT NOT NULL CHECK (cost > 0.0),
  repair VACHAR NOT NULL,
  PRIMARY KEY (id, pid),
  FOREIGN KEY (pid) REFERENCES pools(id)
};