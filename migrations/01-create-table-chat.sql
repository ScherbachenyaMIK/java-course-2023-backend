-- liquibase formatted sql
-- changeset ScherbachenyaMIK:create-table-chat
CREATE TABLE IF NOT EXISTS Chat
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    username        TEXT                        NOT NULL,

    PRIMARY KEY (id)
);
