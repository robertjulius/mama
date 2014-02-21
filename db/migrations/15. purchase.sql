DELETE FROM activity_logs;
DELETE FROM accounts;
DELETE FROM item_stocks;
DELETE FROM migration.purchase_details;
DELETE FROM migration.purchase_headers;

INSERT INTO migration.purchase_headers
(id, last_updated_by, LAST_UPDATED_TIMESTAMP, advance_payment, DISCOUNT, EXPENSES, paid_in_full_flag, remaining_payment, SUB_TOTAL_AMOUNT, total_amount, transaction_number, transaction_timestamp, supplier_id)
SELECT
id, last_updated_by, LAST_UPDATED_TIMESTAMP, advance_payment, DISCOUNT, EXPENSES, paid_in_full_flag, remaining_payment, SUB_TOTAL_AMOUNT, total_amount, transaction_number, transaction_timestamp, supplier_id
FROM minimarketkk.purchase_headers;

INSERT INTO migration.purchase_details
(id, ITEM_CODE, item_id, item_name, order_num, PRICE_PER_UNIT, QUANTITY, total_amount, UNIT, PURCHASE_HEADER_ID)
SELECT
pd.id, pd.ITEM_CODE, it.id, pd.item_name, pd.order_num, pd.PRICE_PER_UNIT, 
pd.QUANTITY, pd.total_amount, pd.UNIT, pd.PURCHASE_HEADER_ID
FROM minimarketkk.purchase_details pd
INNER JOIN minimarketkk.items it ON it.code = pd.item_code;

INSERT INTO migration.item_stocks
(id, quantity, item_id, purchase_detail_id)
SELECT
pd.id, pd.QUANTITY, it.id, pd.id
FROM minimarketkk.purchase_details pd
INNER JOIN minimarketkk.items it ON it.code = pd.item_code;

INSERT INTO migration.accounts
(id, last_updated_by, last_updated_timestamp, debit, credit, entity_id, notes, ref, timestamp, coa_id )
SELECT
  pd.id, pd.last_updated_by, pd.last_updated_timestamp
  , pd.total_amount, 0, pd.id, 'pembelian', '', pd.last_updated_timestamp, 543
FROM minimarketkk.purchase_details pd
INNER JOIN minimarketkk.items it ON it.code = pd.item_code;


SELECT COUNT(1) FROM minimarketkk.purchase_headers;
SELECT COUNT(1) FROM migration.purchase_headers;

SELECT COUNT(1) FROM minimarketkk.purchase_details;
SELECT COUNT(1) FROM migration.purchase_details;

SELECT COUNT(1) FROM minimarketkk.item_stocks;
SELECT COUNT(1) FROM migration.item_stocks;

SELECT COUNT(1) FROM minimarketkk.accounts;
SELECT COUNT(1) FROM migration.accounts;

SELECT 
  kis.id, kis.item_id, ki.name, ki.code
  , mis.id, mis.item_id
FROM minimarketkk.item_stocks kis
INNER JOIN items ki ON kis.item_id = ki.id
LEFT JOIN migration.item_stocks mis ON kis.id = mis.id
WHERE mis.id IS NULL;

SELECT * FROM minimarketkk.purchase_details WHERE item_name = 'SUPER BUBUR AYAM 27 G';
SELECT * FROM minimarketkk.purchase_details WHERE item_id = 358;

SELECT * FROM minimarketkk.item_stocks s
INNER JOIN minimarketkk.items i ON i.id = s.item_id;

SELECT * FROM minimarketkk.item_stocks s
WHERE s.id <> s.item_id;


