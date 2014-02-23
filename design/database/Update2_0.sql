INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('erp.orderResBackReason','退货原因','',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.customer','resChange','客户原因','erp.orderResBackReason','',1, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.shipError','inputError','发货错误','erp.orderResBackReason','',2, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.shipLazy','byCustomer','发货延迟','erp.orderResBackReason','',3, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.res','byCustomer','质量问题','erp.orderResBackReason','',4, b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.BackReason.other','other','其它','erp.orderResBackReason','',5, b'1');
