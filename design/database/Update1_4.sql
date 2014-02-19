ALTER TABLE MINI_ERP.DEPOSITARY DROP FOREIGN KEY DEPOSITARY_ibfk_2;
ALTER TABLE MINI_ERP.DEPOSITARY DROP FOREIGN KEY DEPOSITARY_ibfk_1;

ALTER TABLE MINI_ERP.BATCH_AND_AREA DROP FOREIGN KEY BATCH_AND_AREA_ibfk_1;
ALTER TABLE MINI_ERP.BATCH_AND_AREA DROP FOREIGN KEY BATCH_AND_AREA_ibfk_2;
DROP TABLE MINI_ERP.BATCH_AND_AREA;

ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_1;
ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_2;
ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_3;
ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_4;
ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_5;
ALTER TABLE MINI_ERP.NO_CONVERT_COUNT DROP FOREIGN KEY NO_CONVERT_COUNT_ibfk_6;
DROP TABLE MINI_ERP.NO_CONVERT_COUNT;


DROP TABLE MINI_ERP.DEPOSITARY;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM DROP FOREIGN KEY STOCK_CHANGE_ITEM_ibfk_1;
ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM DROP BATCH;

ALTER TABLE MINI_ERP.PRODUCT_STORE_IN ADD BATCH VARCHAR(32) NULL;

ALTER TABLE MINI_ERP.PRODUCT_STORE_IN
ADD FOREIGN KEY (BATCH)
REFERENCES MINI_ERP.BATCH (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


CREATE TABLE MINI_ERP.NO_CONVERT_COUNT
(
  ID VARCHAR(32) NOT NULL COMMENT 'ID',
  RES_UNIT VARCHAR(32) NOT NULL COMMENT 'RES_UNIT',
  COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
  STOCK_CHANGE VARCHAR(32) COMMENT 'STOCK_CHANGE',
  STOCK VARCHAR(32) COMMENT 'STOCK',
  PREPARE_CHANGE VARCHAR(32) COMMENT 'PREPARE_CHANGE',
  PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '无转换率数量' DEFAULT CHARACTER SET utf8;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
ADD FOREIGN KEY (STOCK_CHANGE)
REFERENCES MINI_ERP.STOCK_CHANGE_ITEM (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
ADD FOREIGN KEY (RES_UNIT)
REFERENCES MINI_ERP.RES_UNIT (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
ADD FOREIGN KEY (STOCK)
REFERENCES MINI_ERP.STOCK (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
ADD FOREIGN KEY (PREPARE_CHANGE)
REFERENCES MINI_ERP.PREPARE_STOCK_CHANGE (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


CREATE TABLE MINI_ERP.ACCOUNT_CHECKOUT
(
  ID VARCHAR(32) NOT NULL COMMENT 'ID',
  C_YEAR INT NOT NULL COMMENT 'C_YEAR',
  C_MONTH INT NOT NULL COMMENT 'C_MONTH',
  CHECKOUT_TIME TIMESTAMP NOT NULL COMMENT 'CHECKOUT_TIME',
  OPER_EMP VARCHAR(32) NOT NULL COMMENT '操作人',
  PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '结账' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CUSTOMER_ACCOUNT_CHECK_OUT
(
  ID VARCHAR(32) NOT NULL COMMENT 'ID',
  BEGINNING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期初余额',
  CLOSING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期末余额',
  DEBIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '借方金额',
  CREDIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '贷方金额',
  CHECKOUT VARCHAR(32) NOT NULL COMMENT 'CHECKOUT',
  CUSTOMER VARCHAR(32) NOT NULL COMMENT 'CUSTOMER',
  PRIMARY KEY (ID),
  CONSTRAINT UNIQUE_CUSTOMER_CHECKOUT UNIQUE (CHECKOUT, CUSTOMER)
) ENGINE = InnoDB COMMENT = '客户结帐记录' DEFAULT CHARACTER SET utf8;

ALTER TABLE MINI_ERP.CUSTOMER_ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (CUSTOMER)
REFERENCES MINI_ERP.CUSTOMER (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.CUSTOMER_ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (CHECKOUT)
REFERENCES MINI_ERP.ACCOUNT_CHECKOUT (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


CREATE TABLE MINI_ERP.STOCK_ACCOUNT_CHECK_OUT
(
  ID VARCHAR(32) NOT NULL COMMENT 'ID',
  BEGINNING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期初余额',
  CLOSING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期末余额',
  DEBIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '借方金额',
  CREDIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '贷方金额',
  STOCK VARCHAR(32) NOT NULL COMMENT 'STOCK',
  CHECKOUT VARCHAR(32) NOT NULL COMMENT 'CHECKOUT',
  PRIMARY KEY (ID),
  CONSTRAINT UNIQUE_STOCK_CHECKOUT UNIQUE (STOCK, CHECKOUT)
) ENGINE = InnoDB COMMENT = '库存结账' DEFAULT CHARACTER SET utf8;


ALTER TABLE MINI_ERP.STOCK_ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (STOCK)
REFERENCES MINI_ERP.STOCK (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.STOCK_ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (CHECKOUT)
REFERENCES MINI_ERP.ACCOUNT_CHECKOUT (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.BATCH_STORE_COUNT DROP FOREIGN KEY BATCH_STORE_COUNT_ibfk_1;
ALTER TABLE MINI_ERP.BATCH_STORE_COUNT DROP FOREIGN KEY BATCH_STORE_COUNT_ibfk_2;
DROP TABLE MINI_ERP.BATCH_STORE_COUNT;

ALTER TABLE MINI_ERP.ORDER_ITEM DROP FOREIGN KEY ORDER_ITEM_ibfk_2;
ALTER TABLE MINI_ERP.ORDER_ITEM DROP RES;

ALTER TABLE MINI_ERP.ORDER_ITEM DROP STORE_RES_ITEM;

ALTER TABLE MINI_ERP.DISPATCH_ITEM DROP STORE_RES_ITEM;

ALTER TABLE MINI_ERP.DISPATCH_ITEM DROP FOREIGN KEY DISPATCH_ITEM_ibfk_2;
ALTER TABLE MINI_ERP.DISPATCH_ITEM DROP RES;


ALTER TABLE MINI_ERP.PRODUCT_STORE_IN DROP FOREIGN KEY PRODUCT_STORE_IN_ibfk_3;
ALTER TABLE MINI_ERP.PRODUCT_STORE_IN DROP BATCH;

ALTER TABLE MINI_ERP.STOCK ADD BATCH VARCHAR(32) NULL;

ALTER TABLE MINI_ERP.STOCK
ADD FOREIGN KEY (BATCH)
REFERENCES MINI_ERP.BATCH (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM ADD BATCH VARCHAR(32) NULL;

ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM
ADD FOREIGN KEY (BATCH)
REFERENCES MINI_ERP.BATCH (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.BATCH DROP FOREIGN KEY BATCH_ibfk_2;
ALTER TABLE MINI_ERP.BATCH DROP RES;

ALTER TABLE MINI_ERP.BATCH ADD STORE_RES VARCHAR(32) NOT NULL;

DELETE FROM BATCH;

ALTER TABLE MINI_ERP.BATCH
ADD FOREIGN KEY (STORE_RES)
REFERENCES MINI_ERP.STORE_RES (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.ORDER_ITEM DROP COST;

/*
INSERT INTO DG_SYSTEM.FUNC_CATEGORY (ID, NAME, ICON, PRIORITY, MEMO) VALUES ('erp.search','统计分析','',90,'');
*/

INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.search.customerShip','客户发货统计','erp.search','','/func/erp/sale/mgr/SaleCustomerShipReport.seam','','400','');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.saler', 'erp.search.customerShip');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_ID, FUN_ID) VALUES ('erp.sale.manager', 'erp.search.customerShip');
