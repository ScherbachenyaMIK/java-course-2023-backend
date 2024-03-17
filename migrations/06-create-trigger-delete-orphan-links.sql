-- liquibase formatted sql
-- changeset ScherbachenyaMIK:create-trigger-delete-orphan-links
CREATE OR REPLACE FUNCTION delete_orphan_links() 
RETURNS TRIGGER
LANGUAGE plpgsql 
AS '
BEGIN
    DELETE FROM Link
    WHERE id NOT IN (SELECT link_id FROM Chat_Link);
    RETURN NULL;
END;
';

-- Create trigger to execute the function before deleting from Chat_Link
CREATE TRIGGER delete_orphan_links_trigger
AFTER DELETE ON Chat_Link
FOR EACH ROW
EXECUTE FUNCTION delete_orphan_links();