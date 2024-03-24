-- liquibase formatted sql
-- changeset ScherbachenyaMIK:alter-table-link-add-coloumn-last-seen
ALTER TABLE Link
ADD COLUMN last_seen TIMESTAMP DEFAULT CURRENT_TIMESTAMP;
