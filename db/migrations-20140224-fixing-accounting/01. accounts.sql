DELETE FROM migration.accounts;

INSERT INTO migration.accounts (
last_updated_by, last_updated_timestamp, credit, debit, entity_id, notes, ref, timestamp, coa_id
)
SELECT 
  hh.last_updated_by
  , hh.last_updated_timestamp
  , 0
  , hh.total_amount
  , hh.id
  , 'pembelian'
  , hh.transaction_number
  , hh.last_updated_timestamp
  , 543
FROM migration.purchase_headers hh;

INSERT INTO migration.accounts (
last_updated_by, last_updated_timestamp, credit, debit, entity_id, notes, ref, timestamp, coa_id
)
SELECT 
  hh.last_updated_by
  , hh.last_updated_timestamp
  , hh.total_amount
  , 0
  , hh.id
  , 'Penjualan'
  , hh.transaction_number
  , hh.last_updated_timestamp
  , 410
FROM migration.sale_headers hh;

