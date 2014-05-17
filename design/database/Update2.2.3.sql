ALTER TABLE MINI_ERP.CHECK_OUT DROP C_YEAR;
ALTER TABLE MINI_ERP.CHECK_OUT DROP C_MONTH;
ALTER TABLE MINI_ERP.CHECK_OUT DROP BEGIN_DAY;

ALTER TABLE MINI_ERP.CHECK_OUT ADD BEGIN_DATE timestamp NOT NULL;
ALTER TABLE MINI_ERP.CHECK_OUT ADD CLOSE_DATE timestamp NOT NULL;

UPDATE MINI_ERP.CUSTOMER SET ADVANCE_MONEY = 0, ACCOUNT_MONEY = 94460 WHERE ID='000232';

UPDATE MINI_ERP.ACCOUNT_OPER SET ADVANCE_RECEIVABLE = 0 , ACCOUNTS_RECEIVABLE = 16590 WHERE ID = 'ff80808145f656110145ff12475800ba';


UPDATE MINI_ERP.CUSTOMER SET ADVANCE_MONEY = 0, ACCOUNT_MONEY = 164444 WHERE ID='000105';

UPDATE MINI_ERP.ACCOUNT_OPER SET ADVANCE_RECEIVABLE = 0 , ACCOUNTS_RECEIVABLE = 50000 WHERE ID = 'ff80808145f656110145ff142b3800c0';


UPDATE MINI_ERP.ACCOUNT_OPER SET ADVANCE_RECEIVABLE = 0 , ACCOUNTS_RECEIVABLE = 50000 WHERE ID = 'ff8080814600b9a90146036ef65d00fb';


# | ID                               | OPER_EMP | OPER_TYPE        | OPER_DATE           | CUSTOM | DESCRIPTION | CUSTOMER_ORDER | ORDER_BACK | ADVANCE_RECEIVABLE | ACCOUNTS_RECEIVABLE | PROXY_ACCOUNTS_RECEIVABLE | SAVEING                          | VERSION | CERTIFICATE |
# +----------------------------------+----------+------------------+---------------------+--------+-------------+----------------+------------+--------------------+---------------------+---------------------------+----------------------------------+---------+-------------+
# | ff80808145f656110145ff12475800ba | ks4      | CUSTOMER_SAVINGS | 2014-05-12 12:00:00 | 000232 | NULL        | NULL           | NULL       |         -94460.000 |          111050.000 |                     0.000 | ff80808145f656110145ff12475800b9 |       0 | NULL        |
# | ff80808145f656110145ff142b3800c0 | ks4      | CUSTOMER_SAVINGS | 2014-05-14 12:00:00 | 000105 | NULL        | NULL           | NULL       |        -214444.000 |          264444.000 |                     0.000 | ff80808145f656110145ff142b3800bf |       0 | NULL        |
# +----------------------------------+----------+------------------+---------------------+--------+-------------+----------------+------------+--------------------+---------------------+---------------------------+----------------------------------+---------+-------------+
