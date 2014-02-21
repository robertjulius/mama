UPDATE
  minimarketkk.sale_headers mini
INNER JOIN migration.sale_headers mig ON mig.pay = mini.pay
AND mig.TOTAL_AMOUNT = mini.TOTAL_AMOUNT
SET 
  mig.transaction_timestamp = mini.transaction_timestamp
  , mig.last_updated_by = mini.last_updated_by
  , mig.last_updated_timestamp = mini.last_updated_timestamp;

UPDATE
  minimarketkk.sale_headers mini
INNER JOIN migration.sale_headers mig ON mig.TOTAL_AMOUNT = mini.TOTAL_AMOUNT
AND mini.total_amount = 55500
SET 
  mig.transaction_timestamp = mini.transaction_timestamp
  , mig.last_updated_by = mini.last_updated_by
  , mig.last_updated_timestamp = mini.last_updated_timestamp;

UPDATE
  migration.accounts aa
INNER JOIN migration.sale_details dd ON aa.entity_id = dd.id
INNER JOIN migration.sale_headers hh ON dd.sale_header_id = hh.id
SET 
  aa.last_updated_by = hh.last_updated_by
  , aa.last_updated_timestamp = hh.last_updated_timestamp
  , aa.timestamp = aa.last_updated_timestamp
WHERE coa_id = 410;