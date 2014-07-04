INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES
  ('erp.search.deliveryFareSearch', '运费查询', 'erp.search', '', '/func/erp/sale/DeliveryFareSearch.seam', '', '530',
   '');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.search.deliveryFareSearch');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.search.deliveryFareSearch');
