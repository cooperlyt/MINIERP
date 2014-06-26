ALTER TABLE MINI_ERP.DISPATCH ADD DELIVERED bit NOT NULL;

UPDATE MINI_ERP.DISPATCH SET DELIVERED = b'0';
UPDATE MINI_ERP.DISPATCH SET DELIVERED = b'1' WHERE (STORE_OUT = b'1') and (not (SEND_TIME is null));


UPDATE MINI_ERP.CUSTOMER SET INIT_AD = 0 WHERE INIT_AD is null;
UPDATE MINI_ERP.CUSTOMER SET INIT_AC = 0 WHERE INIT_AC is null;
UPDATE MINI_ERP.CUSTOMER SET INIT_PAC = 0 WHERE INIT_PAC is null;
