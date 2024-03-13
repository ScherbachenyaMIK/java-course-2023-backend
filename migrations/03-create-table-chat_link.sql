-- liquibase formatted sql
-- changeset ScherbachenyaMIK:create-table-chat_link
CREATE TABLE IF NOT EXISTS Chat_Link
(
    chat_id         BIGINT,
    link_id         BIGINT,

    FOREIGN KEY (chat_id) REFERENCES Chat(id),
    FOREIGN KEY (link_id) REFERENCES Link(id),
    PRIMARY KEY (chat_id, link_id)
);
