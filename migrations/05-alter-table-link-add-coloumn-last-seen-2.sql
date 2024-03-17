-- liquibase formatted sql
-- changeset ScherbachenyaMIK:alter-table-link-add-coloumn-last-seen-2
ALTER TABLE link
ALTER COLUMN last_seen SET NOT NULL;