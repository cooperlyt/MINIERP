
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.search.stockChangeItemGroup','出/入库分组汇总','erp.search','','/func/erp/store/StoreResInOutReport.seam','','600','');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.manager','erp.search.stockChangeItemGroup');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.store','erp.search.stockChangeItemGroup');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.store','erp.search.groupStoreInTotal');