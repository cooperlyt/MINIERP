INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.saler.middleManMgr');

UPDATE DG_SYSTEM.FUNCTION SET LOCATION = '/func/erp/sale/MiddleManMgr.seam' WHERE ID = 'erp.saler.middleManMgr';

UPDATE DG_SYSTEM.ROLE SET NAME = '调度' WHERE ID = 'erp.storage.dispatch';


INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.manager','erp.store.stockSearch');



DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID = 'erp.sale.manager' and  FUN_ID= 'erp.sale.quotedPrice';
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.total.orderMoneyChart');

DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID='erp.sale.manager' and FUN_ID = 'erp.sale.cancelAndbackRes';
DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID = 'erp.sale.manager' and FUN_ID= 'erp.store.stockSearch';


DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID='erp.finance.accountancy' and FUN_ID = 'erp.cashier.moneySave';
DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID='erp.finance.accountancy' and FUN_ID= 'finance.prepareAccount';


DELETE FROM DG_SYSTEM.ROLE_BIZ WHERE ROLE_ID = 'tempsend';
DELETE FROM DG_SYSTEM.ROLE WHERE ID=  'tempsend';


INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.dispatch','erp.store.stockSearch');
DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID = 'erp.sale.saler' and  FUN_ID = 'erp.store.stockSearch';


DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID ='system.manager' and FUN_ID = 'system.role';