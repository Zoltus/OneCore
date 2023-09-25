CREATE TABLE IF NOT EXISTS players (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    uuid       CHAR(36) NOT NULL UNIQUE,
    tpenabled  BOOLEAN NOT NULL DEFAULT 0,
    isvanished BOOLEAN NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS balances (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    uuid    CHAR(36) NOT NULL UNIQUE,
    balance DOUBLE NOT NULL,
    FOREIGN KEY (uuid) REFERENCES players(uuid)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

CREATE TABLE IF NOT EXISTS homes (
    id INTEGER PRIMARY KEY AUTO_INCREMENT,
    uuid  CHAR(36) NOT NULL UNIQUE,
    name  VARCHAR(16) NOT NULL,
    world VARCHAR(255) NOT NULL,
    x     DOUBLE NOT NULL,
    y     DOUBLE NOT NULL,
    z     DOUBLE NOT NULL,
    yaw   FLOAT NOT NULL,
    pitch FLOAT NOT NULL,
    FOREIGN KEY (uuid) REFERENCES players(uuid)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);

    /*
     * mysql needs auto_increment
     * sqlite needs AUTO_INCREMENT
     */