
DELETE FROM DG_SYSTEM.ROLE_FUNCTION WHERE ROL_ID = 'erp.sale.saler' AND FUN_ID='erp.sale.quotedPrice';

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.processInstanceMgr', '流程管理', 'system.manager', '', '/func/system/jbpm/ProcessInstanceMgr.seam', '', '6', '');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('system.manager', 'system.processInstanceMgr');