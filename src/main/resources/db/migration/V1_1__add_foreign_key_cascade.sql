ALTER TABLE todo DROP FOREIGN KEY todo_ibfk_1;

ALTER TABLE todo ADD FOREIGN KEY (member_id) REFERENCES member (id) ON DELETE CASCADE;