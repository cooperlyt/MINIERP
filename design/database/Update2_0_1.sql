
UPDATE DG_SYSTEM.BUSINESS_DEFINE SET START_PAGE='/business/startPrepare/erp/sale/StoreResBackCreate.xhtml', START_PROPAGATION = NULL WHERE ID ='erp.business.orderCancel';

INSERT INTO DG_SYSTEM.BUSINESS_DEFINE(ID, NAME, WF_NAME, START_PAGE, START_DATA_VALIDATOR, TASK_SERVICE, CATEGORY, MEMO, VERSION) VALUES('erp.business.allocation','仓库调拨','stockAllocation','/business/startPrepare/erp/store/AllocationCreate.xhtml','','','erp.storage','仓库调拨',0);

INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('erp.allocationReason','调库原因','',b'1');

INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.allocationReason.order','order','订单发货调库','erp.allocationReason','',1, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.allocationReason.full','full','仓库储存空间不足','erp.allocationReason','',2, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.allocationReason.save','save','售销备货','erp.allocationReason','',3, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.allocationReason.task','task','日常调库','erp.allocationReason','',4, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.allocationReason.other','other','其它原因','erp.allocationReason','',5, b'1');


INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.store.allocation','仓库调拨','erp.storage.store','','/func/erp/store/StoreAllocation.seam','','4','');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.storage.store', 'erp.store.allocation');


DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE  ROL_ID = 'erp.storage.store' AND FUN_ID  = 'erp.search.groupStoreInTotal';

UPDATE DG_SYSTEM.FUNCTION SET NAME='发货退货报表' WHERE ID='erp.search.customerShip';
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.search.sallInOutDayReoper','客户往来日报','erp.search','','/func/erp/sale/InOutDayReport.seam','','500','');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.search.customerMoneyReport','客户货款汇总','erp.search','','/func/erp/sale/CustomerMoneyReport.seam','','500','');


INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.finance.cashier','erp.search.sallInOutDayReoper');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.search.sallInOutDayReoper');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.search.sallInOutDayReoper');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.search.customerMoneyReport');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.search.customerMoneyReport');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.finance.cashier','erp.search.customerMoneyReport');
