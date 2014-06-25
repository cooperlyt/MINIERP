INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES
  ('erp.search.storeChangeTotal', '仓库货品出入库汇总', 'erp.search', '', '/func/erp/store/StoreChangeTotal.seam', '', '620',
   '');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.manager', 'erp.search.storeChangeTotal');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.store', 'erp.search.storeChangeTotal');