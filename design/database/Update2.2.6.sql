ALTER TABLE MINI_ERP.RES_SALE_REBATE ADD ITEM_REBATE DECIMAL(19,4) NOT NULL;

ALTER TABLE MINI_ERP.RES_SALE_REBATE DROP PRICE;




INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'system.processInstanceMgr');
UPDATE DG_SYSTEM.FUNCTION SET LOCATION = '/func/erp/finance/Accounting.seam' where ID ='finance.account';

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.finance.cashier', 'finance.account');
UPDATE DG_SYSTEM.FUNCTION SET LOCATION = '/func/system/ProcessInstanceMgr.seam' where ID ='system.processInstanceMgr';