ALTER TABLE executions
ALTER
COLUMN "state_duration" FLOAT GENERATED ALWAYS AS (JQ_DOUBLE("value", '.state.duration'));
CREATE INDEX IF NOT EXISTS executions_end_date_sort ON executions ("end_date" ASC NULLS FIRST);
