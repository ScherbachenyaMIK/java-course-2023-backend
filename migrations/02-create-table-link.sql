-- liquibase formatted sql
-- changeset ScherbachenyaMIK:create-table-link
CREATE TABLE IF NOT EXISTS Link
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    link            TEXT                        NOT NULL,
    last_update     TIMESTAMP                   NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (link)
);
