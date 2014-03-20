INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.store.stockSearch');

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.sale.orderRebateProgram','提成方案','erp.sale.mgr','','/func/erp/sale/mgr/OrderRebateProgramMgr.seam','','20','');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.sale.orderRebateProgram');