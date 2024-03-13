-- liquibase formatted sql
-- changeset ScherbachenyaMIK:create-table-chat
CREATE TABLE IF NOT EXISTS Chat
(
    id              BIGINT GENERATED ALWAYS AS IDENTITY,
    chat_id         BIGINT                     NOT NULL,

    PRIMARY KEY (id),
	UNIQUE (chat_id)
);
