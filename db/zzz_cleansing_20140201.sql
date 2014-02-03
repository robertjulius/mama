BACKUP DULU... JANGAN LUPA...!!!

use minimarketkk;

SELECT hh.TRANSACTION_TIMESTAMP, dd.ITEM_NAME, dd.QUANTITY
FROM purchase_headers hh
INNER JOIN purchase_details dd ON hh.id = dd.purchase_header_id
AND hh.transaction_timestamp > STR_TO_DATE('28-01-2014', '%d-%m-%Y');

SELECT hh.TRANSACTION_TIMESTAMP, dd.ITEM_NAME, dd.QUANTITY
FROM sale_headers hh
INNER JOIN sale_details dd ON hh.id = dd.sale_header_id;

SELECT items.name, item_stocks.stock
FROM items INNER JOIN item_stocks ON items.id = item_stocks.item_id
ORDER BY items.id;

SELECT ii.name, dd.QUANTITY
FROM purchase_details dd INNER JOIN items ii
ON dd.ITEM_CODE = ii.code
ORDER BY ii.id;

UPDATE
  stock_opname_details d
  INNER JOIN item_stocks i ON d.item_stock_id = i.id
  AND d.stock_opname_header_id = 2
SET
	i.stock = d.quantity_system
	, i.hpp = 0;

SELECT items.name, item_stocks.stock
FROM items INNER JOIN item_stocks ON items.id = item_stocks.item_id
ORDER BY items.id;

SELECT ii.name, dd.QUANTITY
FROM purchase_details dd INNER JOIN items ii
ON dd.ITEM_CODE = ii.code
ORDER BY ii.id;