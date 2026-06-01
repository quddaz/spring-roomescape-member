CREATE TABLE theme
(
    id            BIGINT       NOT NULL AUTO_INCREMENT,
    name          VARCHAR(255) NOT NULL,
    description   VARCHAR(255) NOT NULL,
    thumbnail_url VARCHAR(255) NOT NULL,
    PRIMARY KEY (id),
    UNIQUE (name)
);

CREATE TABLE reservation_time
(
    id       BIGINT NOT NULL AUTO_INCREMENT,
    start_at TIME   NOT NULL,
    theme_id BIGINT NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (theme_id) REFERENCES theme (id),
    UNIQUE (theme_id, start_at)
);

CREATE TABLE reservation_slot
(
    id      BIGINT       NOT NULL AUTO_INCREMENT,
    date    DATE         NOT NULL,
    time_id BIGINT       NOT NULL,
    PRIMARY KEY (id),
    FOREIGN KEY (time_id) REFERENCES reservation_time (id),
    UNIQUE (date, time_id)
);

CREATE TABLE reservation_confirmed
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255) NOT NULL,
    reservation_slot_id BIGINT NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (reservation_slot_id) REFERENCES reservation_slot (id),
    UNIQUE (reservation_slot_id)
);

CREATE TABLE reservation_waiting
(
    id                  BIGINT       NOT NULL AUTO_INCREMENT,
    name                VARCHAR(255) NOT NULL,
    reservation_slot_id BIGINT NOT NULL,
    created_at          TIMESTAMP DEFAULT CURRENT_TIMESTAMP,

    PRIMARY KEY (id),
    FOREIGN KEY (reservation_slot_id) REFERENCES reservation_slot (id),
    UNIQUE (reservation_slot_id, name)
);
