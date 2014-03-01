INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('erp.orderResBackReason','退货原因','',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.customer','resChange','客户原因','erp.orderResBackReason','',1, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.shipError','inputError','发货错误','erp.orderResBackReason','',2, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.shipLazy','byCustomer','发货延迟','erp.orderResBackReason','',3, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.res','byCustomer','质量问题','erp.orderResBackReason','',4, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.other','other','其它','erp.orderResBackReason','',5, b'1');


INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.sale.cancelAndbackRes','撤单&退货','erp.sale.mgr','','/func/erp/sale/CancelOrderAndBackRes.seam','','10','');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.sale.cancelAndbackRes');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.sale.cancelAndbackRes');

UPDATE DG_SYSTEM.BUSINESS_DEFINE SET NAME='撤单&退货' WHERE id ='erp.business.orderCancel';

UPDATE DG_SYSTEM.FUNCTION SET LOCATION='/func/erp/finance/accountancy/AccountingMgr.seam' WHERE ID = 'finance.config.accounting';
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('finance.config.bank','银行帐户','finance.config','','/func/erp/finance/BankAccountMgr.seam','','2','');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.finance.accountancy','finance.config.bank');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.finance.cashier','finance.config.bank');