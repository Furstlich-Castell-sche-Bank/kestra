-- make state_duration nullable
ALTER TABLE executions ALTER COLUMN "state_duration" DROP NOT NULL;
-- speed up sorting by duration, it should work for every combination of ASC/DESC and NULLS FIRST/LAST
CREATE INDEX IF NOT EXISTS executions_state_duration_sort ON executions ("state_duration" ASC);
