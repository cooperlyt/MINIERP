INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.store.stockSearch');

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.sale.orderRebateProgram','销售员提成方案','erp.sale.mgr','','/func/erp/sale/mgr/OrderRebateProgramMgr.seam','','20','');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.sale.orderRebateProgram');

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.sale.orderRebateCalc','销售员提成计算','erp.sale.mgr','','/func/erp/sale/mgr/MiddleReward.seam','','22','');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.sale.orderRebateCalc');


INSERT INTO DG_SYSTEM.SYSTEM_PARAM(ID,TYPE,VALUE,MEMO) VALUES('erp.customer.balanceInput','BOOLEAN','true','是否可修改客户帐户余额');