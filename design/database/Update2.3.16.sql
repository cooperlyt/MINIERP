
UPDATE DG_SYSTEM.SYSTEM_PARAM SET VALUE = '27' , MEMO ='结算起始日 例： 结算日为 27 则2月的 2015-1-27 至 2015-2-26 日'
WHERE ID='erp.finance.beginningDay';

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.finance.beginUpMonth', 'BOOLEAN', 'true', '结算起始日是否为上月日期 例：true: 结算日为 27 则2月的 2015-1-27 至 2015-2-26 日');



INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO)
VALUES ('erp.total.monthMoneyChart', '月销售分析图', 'erp.total', '', '/func/erp/total/MonthMoneyChart.seam', '', '140', '');

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO)
VALUES ('erp.total.yearMoneyChart', '年销售分析图', 'erp.total', '', '/func/erp/total/YearMoneyChart.seam', '', '140', '');


INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.total.monthMoneyChart');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.total.yearMoneyChart');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.total.monthMoneyChart');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.total.yearMoneyChart');
