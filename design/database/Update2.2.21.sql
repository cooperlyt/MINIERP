DROP TABLE MINI_ERP.STOCK_ACCOUNT;


CREATE TABLE MINI_ERP.INVENTORY_ITEM
(
	INVENTORY VARCHAR(32) NOT NULL COMMENT 'INVENTORY',
	STOCK VARCHAR(32) NOT NULL COMMENT 'STOCK',
	BEFOR_COUNT DECIMAL(19,4) NOT NULL COMMENT '期初数量',
	LAST_COUNT DECIMAL(19,4) NOT NULL COMMENT '期末数量',
	CHANGE_ITEM VARCHAR(32) COMMENT '盘盈/盘亏项',
	-- 平/盘盈/盘亏
	CHANGE_TYPE VARCHAR(20) NOT NULL COMMENT '更改类型 : 平/盘盈/盘亏',
	CHANGE_COUNT DECIMAL(19,4) COMMENT 'CHANGE_COUNT',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (INVENTORY, STOCK)
) ENGINE = InnoDB COMMENT = 'INVENTORY_ITEM' DEFAULT CHARACTER SET utf8;


ALTER TABLE MINI_ERP.INVENTORY_ITEM
	ADD FOREIGN KEY (INVENTORY)
	REFERENCES MINI_ERP.INVENTORY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.INVENTORY_ITEM
ADD FOREIGN KEY (STOCK)
REFERENCES MINI_ERP.STOCK (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;



ALTER TABLE MINI_ERP.INVENTORY_ITEM
ADD FOREIGN KEY (CHANGE_ITEM)
REFERENCES MINI_ERP.STOCK_CHANGE_ITEM (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.INVENTORY MODIFY COLUMN CHECK_DATE datetime NOT NULL;
UPDATE MINI_ERP.INVENTORY SET CHECK_DATE = APPLY_DATE;

ALTER TABLE MINI_ERP.INVENTORY ADD CHECK_EMP VARCHAR(32) NULL;


ALTER TABLE MINI_ERP.INVENTORY ADD STATUS VARCHAR(20) NOT NULL;
UPDATE MINI_ERP.INVENTORY SET STATUS = 'STORE_INVERTORY';



INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO)
VALUES ('erp.total.resYearChart', '产品年销售趋势', 'erp.total', '', '/func/erp/total/YearResSaleChart.seam', '', '140', '');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.total.resYearChart');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.total.resYearChart');

