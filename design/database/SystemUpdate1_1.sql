
/*MOVE STOCK SEARCH TO RESMGR */
UPDATE DG_SYSTEM.FUNCTION SET CATEGORY = 'erp.res.mgr', PRIORITY  = 20 WHERE ID = 'erp.store.stockSearch'
DELETE FROM DG_SYSTEM.FUNC_CATEGORY WHERE ID = 'erp.search'

INSERT INTO DG_SYSTEM.ROLE (ID, NAME, DESCRIPTION,PRIORITY) VALUES ('erp.produce.factory.mgr','工厂主管','',32);

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.product.factoryMgr','工厂管理','erp.produce.mgr','','/func/erp/product/mgr/FactoryMgr.seam','','1','');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.product.factoryGroup','生厂小组管理','erp.produce.mgr','','/func/erp/product/ProductGroupMgr.seam','','2','');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.produce.manager','erp.product.factoryMgr');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.produce.manager','erp.product.factoryGroup');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.produce.factory.mgr','erp.product.factoryGroup');
