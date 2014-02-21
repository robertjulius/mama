
SELECT
  mig.pay
  , mini.pay
  , mig.TRANSACTION_TIMESTAMP mig_timestamp
  , mini.TRANSACTION_TIMESTAMP mini_timestamp
  , mig.LAST_UPDATED_BY mig_by
  , mini.LAST_UPDATED_BY mini_by
  , mig.LAST_UPDATED_TIMESTAMP mig_update
  , mini.LAST_UPDATED_TIMESTAMP mini_update
FROM migration.sale_headers mig
INNER JOIN minimarketkk.sale_headers mini
ON mig.total_amount = mini.total_amount
AND mig.pay = mini.pay;