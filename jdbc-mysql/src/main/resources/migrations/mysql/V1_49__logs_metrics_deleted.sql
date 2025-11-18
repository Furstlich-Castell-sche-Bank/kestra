ALTER TABLE logs DROP INDEX ix_execution_id;
ALTER TABLE logs DROP INDEX ix_execution_id__task_id;
ALTER TABLE logs DROP INDEX ix_execution_id__taskrun_id;
ALTER TABLE logs DROP INDEX ix_namespace_flow;

ALTER table logs drop column `deleted`;

CREATE INDEX ix_execution_id ON logs (`execution_id`);
CREATE INDEX ix_execution_id__task_id ON logs (`execution_id`, `task_id`);
CREATE INDEX ix_execution_id__taskrun_id ON logs (`execution_id`, `taskrun_id`);
CREATE INDEX ix_namespace_flow ON logs (`tenant_id`, `timestamp`, `level`, `namespace`, `flow_id`);


ALTER TABLE metrics DROP INDEX IF EXISTS ix_metrics_flow_id;
ALTER TABLE metrics DROP INDEX IF EXISTS ix_metrics_execution_id;
ALTER TABLE metrics DROP INDEX IF EXISTS ix_metrics_timestamp;

ALTER TABLE metrics drop column `deleted`;

CREATE INDEX ix_metrics_flow_id ON metrics (`tenant_id`, `namespace`, `flow_id`);
CREATE INDEX ix_metrics_execution_id ON metrics (`execution_id`);
CREATE INDEX ix_metrics_timestamp ON metrics (`tenant_id`, `timestamp`);