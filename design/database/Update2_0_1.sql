
UPDATE DG_SYSTEM.BUSINESS_DEFINE SET START_PAGE='/business/startPrepare/erp/sale/StoreResBackCreate.xhtml', START_PROPAGATION = NULL WHERE ID ='erp.business.orderCancel';

INSERT INTO DG_SYSTEM.BUSINESS_DEFINE(ID, NAME, WF_NAME, START_PAGE, START_DATA_VALIDATOR, TASK_SERVICE, CATEGORY, MEMO, VERSION) VALUES('erp.business.allocation','仓库调拨','stockAllocation','/business/startPrepare/erp/store/AllocationCreate.xhtml','','','erp.storage','仓库调拨',0);