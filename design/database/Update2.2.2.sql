INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ad.DEPOSIT_BACK', '%s 退预存款', '112201', '退预存款 预收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.s.DEPOSIT_BACK', '%s 退款存入', '112201', '退预存款');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.pac.PROXY_SAVINGS', '%s 代收款', '112201', '代收存入 代收应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.s.PROXY_SAVINGS', '%s 代收存入', '112201', '代收存入');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ad.CUSTOMER_SAVINGS', '%s 客户预存', '112201', '客户存款 预收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.CUSTOMER_SAVINGS', '%s 客户支付', '112201', '客户存款 应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.o.CUSTOMER_SAVINGS', '%s 客户存入', '112201', '客户存款');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ad.DEPOSIT_PAY', '%s 支付货款', '112201', '支付货款 预收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.DEPOSIT_PAY', '%s 支付货款', '112201', '支付货款 应收账款');


INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.mf.MONEY_FREE', '%s 减免', '112201', '减免 管理费用');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.MONEY_FREE', '%s 减免货款', '112201', '减免 应收账款');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ad.ORDER_PAY', '%s 支付货款', '112201', '订单支付 预收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.ORDER_PAY', '%s 赊款发货', '112201', '订单支付 应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.pac.ORDER_PAY', '%s 代收发货', '112201', '订单支付 应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.rm.ORDER_PAY', '%s 赊发货款', '112201', '支付货款 主营业务收入');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.ORDER_BACK', '%s 退货冲账', '112201', '退货退款 应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.rm.ORDER_BACK', '%s 退货冲账', '112201', '退货退款 主营业务收入');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ad.ORDER_BACK', '%s 退货存入', '112201', '退货退款 预收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO)
VALUES ('erp.ADF.ac.c.ORDER_BACK', '%s 退货退款', '112201', '退货退款 应收账款');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO) VALUES ('erp.ADF.o.ORDER_BACK', '%s 退货退款', '112201', '退货退款');

INSERT INTO DG_SYSTEM.SYSTEM_PARAM (ID, TYPE, VALUE, MEMO) VALUES ('erp.finance.mgrFee', 'STRING', '6602', '管理费用');


CREATE TABLE MINI_ERP.SALE_PREPARED
(
  _WORD            VARCHAR(10) NOT NULL
  COMMENT '凭证字',
  _CODE            INT         NOT NULL
  COMMENT '凭证号',
  CERTIFICATE_DATE DATETIME    NOT NULL
  COMMENT '凭证时间',
  PREPARED_EMP     VARCHAR(32) NOT NULL
  COMMENT '记帐人',
  APPROVED_EMP     VARCHAR(32) COMMENT '核准',
  CHECKED_EMP      VARCHAR(32) COMMENT '审核',
  CASHIER          VARCHAR(32) COMMENT '出纳',
  MEMO             VARCHAR(200) COMMENT '备注',
  VOUCHERS_COUNT   INT COMMENT '附票数',
  ID               VARCHAR(32) NOT NULL
  COMMENT 'ID',
  PRIMARY KEY (ID)
)
  ENGINE = InnoDB
  COMMENT = '销售记帐'
  DEFAULT CHARACTER SET utf8;

ALTER TABLE MINI_ERP.ACCOUNT_OPER ADD CERTIFICATE VARCHAR(32) NULL;
ALTER TABLE MINI_ERP.MONEY_SAVE ADD CERTIFICATE VARCHAR(32) NULL;

ALTER TABLE MINI_ERP.ACCOUNT_OPER
ADD FOREIGN KEY (CERTIFICATE)
REFERENCES MINI_ERP.SALE_PREPARED (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT;


ALTER TABLE MINI_ERP.MONEY_SAVE
ADD FOREIGN KEY (CERTIFICATE)
REFERENCES MINI_ERP.SALE_PREPARED (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT;


--  switch BankAccount ID

USE MINI_ERP;

ALTER TABLE MINI_ERP.MONEY_SAVE DROP FOREIGN KEY MONEY_SAVE_ibfk_2;
ALTER TABLE MINI_ERP.MONEY_SAVE DROP FOREIGN KEY MONEY_SAVE_ibfk_4;

ALTER TABLE MINI_ERP.BANK_ACCOUNT DROP PRIMARY KEY;

ALTER TABLE MINI_ERP.BANK_ACCOUNT ADD ID VARCHAR(32) NOT NULL;


DELIMITER //

CREATE PROCEDURE CALC_REVEIVE_MONEY()
  BEGIN
    DECLARE Done INT DEFAULT 0;

    DECLARE _AID VARCHAR(32);

    DECLARE _NEW_ID INT DEFAULT 1;

-- 声明两个游标，第二个游标使用到第一个的查询结果
    DECLARE order_csr CURSOR FOR SELECT
                                   NUMBER
                                 FROM BANK_ACCOUNT;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET Done = 1;

    OPEN order_csr;
-- 第一个循环
    order_loop: LOOP -- Loop through org_grade
      FETCH order_csr
      INTO _AID;
      IF Done = 1
      THEN
        LEAVE order_loop;
      END IF;

      UPDATE BANK_ACCOUNT
      SET ID = CONCAT('0', _NEW_ID)
      WHERE NUMBER = _AID;

      SET _NEW_ID = _NEW_ID + 1;

-- 结束第二个循环
    END LOOP order_loop;
    CLOSE order_csr;
  END;
//
DELIMITER ;

CALL CALC_REVEIVE_MONEY();

DROP PROCEDURE CALC_REVEIVE_MONEY;


ALTER TABLE MINI_ERP.BANK_ACCOUNT ADD PRIMARY KEY (ID);

INSERT INTO DG_SYSTEM.WORD (ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE)
VALUES ('erp.bank.other', 'OTHER', '其它', 'erp.bank', '', 99, b'0');

INSERT INTO MINI_ERP.BANK_ACCOUNT (NUMBER, BANK, ENABLE, ID) VALUES (' ', 'erp.bank.other', b'0', 99);


DELIMITER //

CREATE PROCEDURE CALC_REVEIVE_MONEY()
  BEGIN
    DECLARE Done INT DEFAULT 0;

    DECLARE _AID VARCHAR(32);

    DECLARE _OID VARCHAR(100);


-- 声明两个游标，第二个游标使用到第一个的查询结果
    DECLARE order_csr CURSOR FOR SELECT
                                   ID,
                                   NUMBER
                                 FROM BANK_ACCOUNT;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET Done = 1;

    OPEN order_csr;
-- 第一个循环
    order_loop: LOOP -- Loop through org_grade
      FETCH order_csr
      INTO _AID, _OID;
      IF Done = 1
      THEN
        LEAVE order_loop;
      END IF;


      UPDATE MONEY_SAVE
      SET NUMBER = _AID
      WHERE NUMBER = _OID;


-- 结束第二个循环
    END LOOP order_loop;
    CLOSE order_csr;
  END;
//
DELIMITER ;

CALL CALC_REVEIVE_MONEY();

DROP PROCEDURE CALC_REVEIVE_MONEY;


ALTER TABLE MINI_ERP.MONEY_SAVE
ADD FOREIGN KEY (NUMBER)
REFERENCES MINI_ERP.BANK_ACCOUNT (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT;


UPDATE MINI_ERP.MONEY_SAVE
SET NUMBER = '99'
WHERE MONEY_SAVE.PAY_TYPE = 'BANK_TRANSFER' AND NUMBER IS NULL;

--   ---------- customer ID
SET FOREIGN_KEY_CHECKS = 0;

DELIMITER //

CREATE PROCEDURE CALC_REVEIVE_MONEY()
  BEGIN
    DECLARE Done INT DEFAULT 0;

    DECLARE _AID VARCHAR(32);

    DECLARE _OID VARCHAR(32);

    DECLARE _NEWID VARCHAR(32);

    DECLARE _INDEX INT DEFAULT 0;


-- 声明两个游标，第二个游标使用到第一个的查询结果
    DECLARE order_csr CURSOR FOR SELECT
                                   ID
                                 FROM CUSTOMER;

    DECLARE CONTINUE HANDLER FOR NOT FOUND SET Done = 1;

    OPEN order_csr;
-- 第一个循环
    order_loop: LOOP -- Loop through org_grade
      FETCH order_csr
      INTO _AID;
      IF Done = 1
      THEN
        LEAVE order_loop;
      END IF;

      SET _INDEX = _INDEX + 1;

      SET _NEWID = CONCAT('00000', _INDEX);
      IF _INDEX > 9
      THEN
        SET _NEWID = CONCAT('0000', _INDEX);
      END IF;
      IF _INDEX > 99
      THEN
        SET _NEWID = CONCAT('000', _INDEX);
      END IF;

      IF _INDEX > 999
      THEN
        SET _NEWID = CONCAT('00', _INDEX);
      END IF;

      IF _INDEX > 9999
      THEN
        SET _NEWID = CONCAT('0', _INDEX);
      END IF;

      IF _INDEX > 99999
      THEN
        SET _NEWID = CONCAT('', _INDEX);
      END IF;


      UPDATE CUSTOMER
      SET ID = _NEWID  WHERE ID = _AID;

      UPDATE CUSTOMER_CONTACT SET CUSTOMER = _NEWID WHERE CUSTOMER = _AID;

      UPDATE ACCOUNT_OPER SET ACCOUNT_OPER.CUSTOM = _NEWID WHERE CUSTOM = _AID;

      UPDATE QUOTED_PRICE SET QUOTED_PRICE.CUSTOMER = _NEWID WHERE CUSTOMER = _AID;

      UPDATE CUSTOMER_ORDER SET CUSTOMER_ID = _NEWID WHERE CUSTOMER_ID = _AID;

      UPDATE ORDER_BACK SET CUSTOMER = _NEWID WHERE CUSTOMER = _AID;


-- 结束第二个循环
    END LOOP order_loop;
    CLOSE order_csr;
  END;
//
DELIMITER ;

CALL CALC_REVEIVE_MONEY();

DROP PROCEDURE CALC_REVEIVE_MONEY;


SET FOREIGN_KEY_CHECKS = 1;


-- re Create account table


ALTER TABLE MINI_ERP.ACCOUNT_CHECK_OUT DROP FOREIGN KEY ACCOUNT_CHECK_OUT_ibfk_1;
ALTER TABLE MINI_ERP.ACCOUNT_CHECK_OUT DROP FOREIGN KEY ACCOUNT_CHECK_OUT_ibfk_2;
DROP TABLE MINI_ERP.ACCOUNT_CHECK_OUT;

DROP TABLE MINI_ERP.CHECK_OUT;

DROP TABLE MINI_ERP.ACCOUNT;


CREATE TABLE MINI_ERP.ACCOUNT_CHECK_OUT
(
  ID VARCHAR(32) NOT NULL COMMENT 'ID',
  BEGINNING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期初余额',
  BEGINNING_COUNT DECIMAL(19,4) NOT NULL COMMENT '期初数量',
  CLOSING_BALANCE DECIMAL(19,4) NOT NULL COMMENT '期末余额',
  CLOSING_COUNT DECIMAL(19,4) NOT NULL COMMENT '期末数量',
  DEBIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '借方金额',
  DEBIT_COUNT DECIMAL(19,4) NOT NULL COMMENT '借方数量',
  CREDIT_MONEY DECIMAL(19,4) NOT NULL COMMENT '贷方金额',
  CREDIT_COUNT DECIMAL(19,4) NOT NULL COMMENT '贷方数量',
  VERSION INT COMMENT 'VERSION',
  ACCOUNT_CODE VARCHAR(20) NOT NULL COMMENT '科目代码',
  CHECKOUT INT NOT NULL COMMENT 'CHECKOUT',
  PRIMARY KEY (ID),
  CONSTRAINT UNIQUE_CUSTOMER_CHECKOUT UNIQUE (ACCOUNT_CODE, CHECKOUT)
) ENGINE = InnoDB COMMENT = '科目结帐' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CHECK_OUT
(
  C_YEAR INT NOT NULL COMMENT 'C_YEAR',
  C_MONTH INT NOT NULL COMMENT 'C_MONTH',
  CHECKOUT_TIME TIMESTAMP NOT NULL COMMENT 'CHECKOUT_TIME',
  OPER_EMP VARCHAR(32) NOT NULL COMMENT '操作人',
  BEGIN_DAY INT NOT NULL COMMENT '结算日',
  VERSION INT COMMENT 'VERSION',
  ID BIGINT NOT NULL COMMENT 'ID',
  PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '结账' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ACCOUNT
(
  ACCOUNT_CODE VARCHAR(20) NOT NULL COMMENT '科目代码',
  ACCOUNT_LEVEL INT NOT NULL COMMENT '科目级别',
  ACCOUNT_NAME VARCHAR(50) NOT NULL COMMENT '科目名称',
  DIRECTION VARCHAR(10) NOT NULL COMMENT '方向',
  PRIMARY KEY (ACCOUNT_CODE)
) ENGINE = InnoDB COMMENT = 'ACCOUNT' DEFAULT CHARACTER SET utf8;

ALTER TABLE MINI_ERP.ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (CHECKOUT)
REFERENCES MINI_ERP.CHECK_OUT (ID)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

ALTER TABLE MINI_ERP.ACCOUNT_CHECK_OUT
ADD FOREIGN KEY (ACCOUNT_CODE)
REFERENCES MINI_ERP.ACCOUNT (ACCOUNT_CODE)
  ON UPDATE RESTRICT
  ON DELETE RESTRICT
;

