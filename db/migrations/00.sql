SELECT
  header.id
  , detail.item_name
  , detail.quantity
  , detail.price_per_unit
  , detail.unit
  , detail.total_amount
  , header.advance_payment
  , header.sub_total_amount
  , header.total_amount
  , header.last_updated_by
FROM purchase_headers header
INNER JOIN purchase_details detail ON header.id = detail.purchase_header_id;



SELECT
  header.id
  , detail.item_code
  , detail.item_name
  , detail.quantity
  , header.sub_total_amount
  , header.total_amount
  , header.pay
  , header.money_change
  , header.last_updated_by
  , header.last_updated_timestamp
FROM minimarketkk.sale_headers header
INNER JOIN minimarketkk.sale_details detail ON header.id = detail.sale_header_id
ORDER BY header.last_updated_timestamp;


