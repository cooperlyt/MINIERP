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