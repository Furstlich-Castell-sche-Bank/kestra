-- speed up sorting by end_date, it should work for every combination of ASC/DESC and NULLS FIRST/LAST
CREATE INDEX IF NOT EXISTS executions_end_date_sort ON executions (end_date ASC NULLS FIRST);