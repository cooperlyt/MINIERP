SET SESSION FOREIGN_KEY_CHECKS=0;

/* Drop Tables */

DROP TABLE MINI_ERP.ALLOCATION_RES;
DROP TABLE MINI_ERP.INVENTORY;
DROP TABLE MINI_ERP.STORE_CHANGE;
DROP TABLE MINI_ERP.MATERIAL_STORE_OUT;
DROP TABLE MINI_ERP.ALLOCATION;
DROP TABLE MINI_ERP.NO_CONVERT_COUNT;
DROP TABLE MINI_ERP.BATCH_AND_AREA;
DROP TABLE MINI_ERP.DEPOSITARY;
DROP TABLE MINI_ERP.STORE_AREA;
DROP TABLE MINI_ERP.BATCH_STORE_COUNT;
DROP TABLE MINI_ERP.STOCK_CHANGE_ITEM;
DROP TABLE MINI_ERP.BATCH;
DROP TABLE MINI_ERP.SUPPLIER_RES;
DROP TABLE MINI_ERP.SUPPLIER;
DROP TABLE MINI_ERP.MATERIAL_BACK_STORE_IN;
DROP TABLE MINI_ERP.SCRAP_STORE_OUT;
DROP TABLE MINI_ERP.MATERIAL_STORE_IN;
DROP TABLE MINI_ERP.STOCK;
DROP TABLE MINI_ERP.DISPATCH_ITEM;
DROP TABLE MINI_ERP.FORMAT;
DROP TABLE MINI_ERP.PREPARE_STOCK_CHANGE;
DROP TABLE MINI_ERP.ORDER_ITEM;
DROP TABLE MINI_ERP.STORE_RES;
DROP TABLE MINI_ERP.FORMAT_DEFINE;
DROP TABLE MINI_ERP.RES;
DROP TABLE MINI_ERP.RES_CATEGORY;
DROP TABLE MINI_ERP.RES_UNIT;
DROP TABLE MINI_ERP.UNIT_GROUP;
DROP TABLE MINI_ERP.BACK_PREPARE_MONEY;
DROP TABLE MINI_ERP.PREPARE_PAY;
DROP TABLE MINI_ERP.PRODUCT_BACK_STORE_IN;
DROP TABLE MINI_ERP.ORDER_BACK;
DROP TABLE MINI_ERP.ACCOUNT_OPER;
DROP TABLE MINI_ERP.ORDER_FEE;
DROP TABLE MINI_ERP.MIDDLE_MONEY_PAY;
DROP TABLE MINI_ERP.DISPATCH;
DROP TABLE MINI_ERP.NEED_RES;
DROP TABLE MINI_ERP.CUSTOMER_ORDER;
DROP TABLE MINI_ERP.CUSTOMER;
DROP TABLE MINI_ERP.CUSTOMER_LEVEL;
DROP TABLE MINI_ERP.ACCOUNTING;
DROP TABLE MINI_ERP.PRODUCT_TO_DOOR;
DROP TABLE MINI_ERP.EXPRESS_INFO;
DROP TABLE MINI_ERP.EXPRESS_CAR;
DROP TABLE MINI_ERP.EXPRESS_DRIVER;
DROP TABLE MINI_ERP.TRANS_CORP;
DROP TABLE MINI_ERP.CARS;
DROP TABLE MINI_ERP.ASSEMBLY;
DROP TABLE MINI_ERP.PRODUCT_STORE_IN;
DROP TABLE MINI_ERP.STOCK_CHANGE;
DROP TABLE MINI_ERP.STORE;
DROP TABLE MINI_ERP.MIDDLE_MAN;
DROP TABLE MINI_ERP.CUSTOMER_AREA;




/* Create Tables */

CREATE TABLE MINI_ERP.ALLOCATION_RES
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	FLOAT_CONVERSION_RATE DECIMAL(19,10) COMMENT '浮动转换率',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	STORE_RES VARCHAR(32) NOT NULL COMMENT 'STORE_RES',
	ALLOCATION VARCHAR(32) NOT NULL COMMENT 'ALLOCATION',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '申请调拨货品' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.INVENTORY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CHECK_DATE TIMESTAMP NOT NULL COMMENT '盘点日期',
	MEMO VARCHAR(200) COMMENT '备注',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	LOSS_RES VARCHAR(32) COMMENT '盘亏',
	ADD_RES VARCHAR(32) COMMENT '盘盈',
	TYPE VARCHAR(32) NOT NULL COMMENT '盘点类型',
	STOCK_CHANGED BIT(1) NOT NULL COMMENT '库存已改变',
	APPLY_EMP VARCHAR(32) NOT NULL COMMENT '负责人',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '盘点' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STORE_CHANGE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	TYPE VARCHAR(32) NOT NULL COMMENT '类型',
	MEMO VARCHAR(200) COMMENT '备注',
	STORE_CHANGE VARCHAR(32) COMMENT 'STORE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '库存变动' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.MATERIAL_STORE_OUT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	PRODUCT_BATCH VARCHAR(32) COMMENT '产品批次',
	RECEIVE_EMP VARCHAR(32) COMMENT '领料人',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '领料出库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ALLOCATION
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	APPLY_STORE VARCHAR(32) NOT NULL COMMENT '申请仓库',
	TARGET_STORE VARCHAR(32) NOT NULL COMMENT '目标仓库',
	APPLY_EMP VARCHAR(32) NOT NULL COMMENT '申请人',
	ALLOCATION_EMP VARCHAR(32) COMMENT '调拨人',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	MEMO VARCHAR(200) COMMENT '备注',
	STATE VARCHAR(20) NOT NULL COMMENT '状态',
	STORE_IN VARCHAR(32) COMMENT '入库',
	STORE_OUT VARCHAR(32) COMMENT '出库',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	COMPLETE_DATE DATETIME COMMENT '完成时间',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '调拨' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STORE_AREA
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	PARENT VARCHAR(32) COMMENT 'PARENT',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '库位' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.SUPPLIER
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '供应商' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STOCK_CHANGE_ITEM
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE_OUT BIT(1) NOT NULL COMMENT '出货',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	BEFORT_COUNT DECIMAL(19,4) NOT NULL COMMENT '操作前件数',
	AFTER_COUNT DECIMAL(19,4) NOT NULL COMMENT '操作后件数',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	STORE_RES VARCHAR(32) NOT NULL COMMENT 'STORE_RES',
	-- INVENTORY
	STOCK VARCHAR(32) NOT NULL COMMENT '库存 : INVENTORY',
	BATCH VARCHAR(32) COMMENT '批次',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '入库出库项目' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.BATCH
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	PRO_DATE DATETIME COMMENT '生产日期',
	EXP_DATE DATETIME COMMENT '保质期至',
	SUPPLIER VARCHAR(32) COMMENT '供应商',
	PRODUCE BIT(1) NOT NULL COMMENT '是否生产批次',
	STORE_IN BIT(1) NOT NULL COMMENT '是否已入库',
	RES VARCHAR(32) NOT NULL COMMENT 'RES',
	LAST_IN_TIME TIMESTAMP COMMENT '入库时间',
	DEFAULT_BATCH BIT(1) NOT NULL COMMENT '默认批次',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '批次' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.MATERIAL_BACK_STORE_IN
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '退料入库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.SCRAP_STORE_OUT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '报损出库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.MATERIAL_STORE_IN
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	CHECK_EMP VARCHAR(32) COMMENT '检验员',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '收料入库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.SUPPLIER_RES
(
	SUPPLIER VARCHAR(32) NOT NULL COMMENT 'SUPPLIER',
	RES VARCHAR(32) NOT NULL COMMENT 'RES',
	PRIMARY KEY (SUPPLIER, RES)
) ENGINE = InnoDB COMMENT = 'SUPPLIER_RES' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STORE_RES
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	RES VARCHAR(32) NOT NULL COMMENT 'RES',
	CODE VARCHAR(50) NOT NULL UNIQUE COMMENT '编号',
	STORE_WARN DECIMAL(19,4) COMMENT '库存预警',
	EXP_WARN INT COMMENT '过期预警',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	FLOAT_CONVERSION_RATE DECIMAL(19,10) COMMENT '浮动转换率',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '货品' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.FORMAT_DEFINE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	DATA_TYPE VARCHAR(32) NOT NULL COMMENT '数据类型 ',
	PRIORITY INT NOT NULL COMMENT '优先级',
	RES VARCHAR(32) NOT NULL COMMENT 'RES',
	WORD_CATEGORY_ID VARCHAR(32) COMMENT '字典类型ID',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '规格定义' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.FORMAT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	DEFINE VARCHAR(32) NOT NULL COMMENT 'DEFINE',
	FORMAT_VALUE VARCHAR(500) NOT NULL COMMENT '规格值',
	STORE_RES VARCHAR(32) NOT NULL COMMENT 'STORE_RES',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '规格' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.RES
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CATEGORY VARCHAR(32) NOT NULL COMMENT 'CATEGORY',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	CODE VARCHAR(50) NOT NULL UNIQUE COMMENT '编号',
	MASTER_UNIT VARCHAR(32) NOT NULL COMMENT '主计量单位',
	IN_DEFAULT VARCHAR(32) NOT NULL COMMENT '入库默认计量单位',
	OUT_DEFAULT VARCHAR(32) NOT NULL COMMENT '出库默认计量单位',
	UNIT_GROUP VARCHAR(32) NOT NULL COMMENT 'UNIT_GROUP',
	ACCOUNTING VARCHAR(32) COMMENT 'ACCOUNTING',
	BATCH_MGR BIT(1) NOT NULL COMMENT '批次管理',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '物料' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.RES_CATEGORY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CATEGORY VARCHAR(32) COMMENT 'CATEGORY',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	_ROOT BIT(1) NOT NULL COMMENT '_ROOT',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	TYPE VARCHAR(32) NOT NULL COMMENT '货品类型',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '货品种类' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.UNIT_GROUP
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	TYPE VARCHAR(32) NOT NULL COMMENT 'TYPE',
	FLOAT_CONVERT_FORMAT VARCHAR(20) COMMENT '浮动转换率格式',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '计量单位组' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.RES_UNIT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	CONVERSION_RATE DECIMAL(19,10) COMMENT '转换率',
	UNIT_GROUP VARCHAR(32) NOT NULL COMMENT 'UNIT_GROUP',
	PRIORITY INT NOT NULL COMMENT '优先级',
	COUNT_FORMAT VARCHAR(20) NOT NULL COMMENT '数据格式',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '计量单位' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ORDER_ITEM
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE_RES VARCHAR(32) COMMENT 'STORE_RES',
	RES VARCHAR(32) COMMENT 'RES',
	NEED_RES VARCHAR(32) NOT NULL COMMENT 'NEED_RES',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	COST DECIMAL(19,3) NOT NULL COMMENT '单位成本',
	MONEY DECIMAL(19,3) NOT NULL COMMENT '单价',
	REBATE DECIMAL(19,4) NOT NULL COMMENT '单位折扣',
	MIDDLE_MONEY DECIMAL(19,3) COMMENT '中间人佣金',
	MIDDLE_RATE DECIMAL(19,4) COMMENT '提点',
	-- A  总金额 * 提点
	-- B  固定金额
	MIDDLE_CALC_TYPE VARCHAR(32) COMMENT '佣金计算方式 : A  总金额 * 提点
B  固定金额',
	MIDDLE_UNIT VARCHAR(32) COMMENT 'MIDDLE_UNIT',
	MONEY_UNIT VARCHAR(32) NOT NULL COMMENT 'MONEY_UNIT',
	STORE_RES_ITEM BIT(1) NOT NULL COMMENT 'STORE_RES_ITEM',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '订单项' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.BACK_PREPARE_MONEY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	ACCOUNT_OPER VARCHAR(32) NOT NULL COMMENT '帐户操作',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '客户退款' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.PREPARE_PAY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	ACCOUNT_OPER VARCHAR(32) NOT NULL COMMENT '帐户操作',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '货款预存' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CUSTOMER_LEVEL
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	PRIORITY INT NOT NULL COMMENT '优先级',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '客户等级' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ACCOUNTING
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	PARENT VARCHAR(32) COMMENT 'PARENT',
	CURRENCY VARCHAR(32) NOT NULL COMMENT '币种',
	DIRECTION VARCHAR(10) NOT NULL COMMENT '方向',
	ROOT BIT(1) NOT NULL COMMENT 'ROOT',
	TYPE VARCHAR(32) NOT NULL COMMENT 'TYPE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '明细科目' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.DISPATCH_ITEM
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	DISPATCH VARCHAR(32) NOT NULL COMMENT 'DISPATCH',
	STORE_RES VARCHAR(32) NOT NULL COMMENT 'STORE_RES',
	COUNT_UNIT VARCHAR(32) NOT NULL COMMENT 'COUNT_UNIT',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '调度项' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.PRODUCT_TO_DOOR
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CAR VARCHAR(32) NOT NULL COMMENT 'CAR',
	EMP_DRIVER VARCHAR(32) NOT NULL COMMENT 'EMP_DRIVER',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '送货上门' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.EXPRESS_INFO
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	TRANS VARCHAR(32) NOT NULL COMMENT 'TRANS',
	NUMBER VARCHAR(50) COMMENT '单号',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '物流发货' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.EXPRESS_CAR
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CAR_CODE VARCHAR(20) COMMENT '车牌号',
	DRIVER VARCHAR(32) NOT NULL COMMENT 'DRIVER',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '整车发货' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.EXPRESS_DRIVER
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '姓名',
	TEL VARCHAR(50) NOT NULL COMMENT '电话',
	CAR_CODE VARCHAR(20) COMMENT '车牌号',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '整车发货司机' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.TRANS_CORP
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	TEL VARCHAR(50) COMMENT '电话',
	CONTACT VARCHAR(50) COMMENT '联系人',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '物流或快递' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CARS
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	EMP_DRIVER VARCHAR(32) NOT NULL COMMENT 'EMP_DRIVER',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = 'CARS' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.NO_CONVERT_COUNT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	RES_UNIT VARCHAR(32) NOT NULL COMMENT 'RES_UNIT',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	STOCK_CHANGE VARCHAR(32) COMMENT 'STOCK_CHANGE',
	STOCK VARCHAR(32) COMMENT 'STOCK',
	DEPOSITARY VARCHAR(32) COMMENT 'DEPOSITARY',
	BATCH_STORE_COUNT VARCHAR(32) COMMENT 'BATCH_STORE_COUNT',
	PREPARE_CHANGE VARCHAR(32) COMMENT 'PREPARE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '无转换率数量' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.DEPOSITARY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	VERSION INT COMMENT 'VERSION',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	STORE_AREA VARCHAR(32) NOT NULL COMMENT 'STORE_AREA',
	STOCK VARCHAR(32) NOT NULL COMMENT 'STOCK',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '货品储放' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.BATCH_STORE_COUNT
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STOCK VARCHAR(32) NOT NULL COMMENT 'STOCK',
	-- ONETOONE
	BATCH VARCHAR(32) NOT NULL COMMENT 'BATCH : ONETOONE',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	VERSION INT COMMENT 'VERSION',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '批次库存' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STOCK
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	RES VARCHAR(32) NOT NULL COMMENT 'RES',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	VERSION INT COMMENT 'VERSION',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	PRIMARY KEY (ID),
	CONSTRAINT STOCE_STORE_RES_UNIQUE UNIQUE (RES, STORE)
) ENGINE = InnoDB COMMENT = '库存' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.BATCH_AND_AREA
(
	STORE_AREA VARCHAR(32) NOT NULL COMMENT 'STORE_AREA',
	BATCH VARCHAR(32) NOT NULL COMMENT 'BATCH'
) ENGINE = InnoDB COMMENT = 'BATCH_AND_AREA' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ORDER_BACK
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CUSTOMER_ORDER VARCHAR(32) NOT NULL COMMENT '订单',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	VERSION INT COMMENT 'VERSION',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	MEMO VARCHAR(200) COMMENT '备注',
	BACK_MONEY VARCHAR(32) NOT NULL COMMENT 'BACK_MONEY',
	MONEY_COMPLETE BIT(1) NOT NULL COMMENT '退款完成',
	RES_COMPLETE BIT(1) NOT NULL COMMENT '已退货',
	APPLY_EMP VARCHAR(32) NOT NULL COMMENT '申请人',
	BACK_TYPE VARCHAR(32) NOT NULL COMMENT '类型',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '退单' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STORE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	ADDRESS VARCHAR(200) COMMENT '地址',
	TEL VARCHAR(50) COMMENT '电话',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	VERSION INT COMMENT 'VERSION',
	OPEN BIT(1) NOT NULL COMMENT 'OPEN',
	ROLE_ID VARCHAR(32) NOT NULL COMMENT 'ROLE_ID',
	SHIP_ROLE VARCHAR(32) NOT NULL COMMENT '发货角色',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '仓库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.MIDDLE_MAN
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	CONTACT VARCHAR(50) NOT NULL COMMENT '联系人',
	TYPE VARCHAR(32) NOT NULL COMMENT '类型',
	MEMO VARCHAR(200) COMMENT '备注',
	BANK_NUMBER VARCHAR(50) COMMENT '银行帐号',
	TEL VARCHAR(50) COMMENT '电话',
	BANK_INFO VARCHAR(100) COMMENT '开户行',
	BANK VARCHAR(32) COMMENT '银行',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '中间人' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.PRODUCT_STORE_IN
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE_CHANGE VARCHAR(32) NOT NULL COMMENT 'STORE_CHANGE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '产品入库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ASSEMBLY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	MEMO VARCHAR(200) COMMENT '备注',
	STORE_IN VARCHAR(32) NOT NULL COMMENT '出库',
	STORE_OUT VARCHAR(32) COMMENT '入库',
	ASSEMBLY_EMP VARCHAR(32) COMMENT '装配人',
	STATE VARCHAR(20) NOT NULL COMMENT '状态',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	COMPLETE_DATE DATETIME COMMENT '完成时间',
	LOSE_OUT VARCHAR(32) COMMENT '损耗出库',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '组装拆卸' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.NEED_RES
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CUSTOMER_ORDER VARCHAR(32) NOT NULL COMMENT '订单',
	TYPE VARCHAR(32) NOT NULL COMMENT '类型',
	LIMIT_TIME TIMESTAMP COMMENT '限制时间',
	REASON VARCHAR(32) NOT NULL COMMENT '原因',
	MEMO VARCHAR(200) COMMENT '备注',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	DISPATCHED BIT(1) NOT NULL COMMENT '已调拨',
	-- 代收货款扣除运费
	PROXY_FARE DECIMAL(19,3) COMMENT '代收货款扣除运费 : 代收货款扣除运费',
	FARE_BY_CUSTOMER BIT(1) NOT NULL COMMENT '客户承担运费',
	POST_CODE VARCHAR(10) NOT NULL COMMENT '邮编',
	ADDRESS VARCHAR(200) NOT NULL COMMENT '地址',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '产品需求' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.DISPATCH
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NEED_RES VARCHAR(32) NOT NULL COMMENT 'NEED_RES',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	EXPRESS_INFO VARCHAR(32) COMMENT 'EXPRESS_INFO',
	EXPRESS_CAR VARCHAR(32) COMMENT 'EXPRESS_CAR',
	PRODUCT_TO_DOOR VARCHAR(32) COMMENT 'PRODUCT_TO_DOOR',
	STOCK_CHANGE VARCHAR(32) COMMENT 'STOCK_CHANGE',
	-- 快递或物流
	-- 整车发
	-- 送货上门
	-- 自提
	DELIVERY_TYPE VARCHAR(32) NOT NULL COMMENT '发货方式 : 快递或物流
整车发
送货上门
自提',
	SEND_TIME DATETIME COMMENT '发货时间',
	FARE DECIMAL(19,3) COMMENT '运费',
	SEND_EMP VARCHAR(32) COMMENT '发货人',
	STATE VARCHAR(20) NOT NULL COMMENT '状态',
	OUT_CUSTOMER VARCHAR(50) COMMENT '客户自提提货人',
	MEMO VARCHAR(200) COMMENT '备注',
	PRIMARY KEY (ID),
	CONSTRAINT NEED_AND_STORE UNIQUE (NEED_RES, STORE)
) ENGINE = InnoDB COMMENT = '调度' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CUSTOMER_ORDER
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CONTACT VARCHAR(50) NOT NULL COMMENT '联系人',
	TEL VARCHAR(50) NOT NULL COMMENT '电话',
	CUSTOMER_ID VARCHAR(32) NOT NULL COMMENT 'CUSTOMER_ID',
	PAY_TYPE VARCHAR(32) NOT NULL COMMENT '交费方式',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	-- 和财务无关,只做管理使用
	PROFIT DECIMAL(19,3) COMMENT '利润 : 和财务无关,只做管理使用',
	TOTAL_COST DECIMAL(19,3) NOT NULL COMMENT '产品成本',
	MEMO VARCHAR(200) COMMENT '备注',
	VERSION INT COMMENT 'VERSION',
	INCLUDE_MIDDLE_MAN BIT(1) NOT NULL COMMENT '带入中间人',
	ORDER_EMPLOYEE VARCHAR(32) NOT NULL COMMENT '签单人',
	TOTAL_MONEY DECIMAL(19,3) COMMENT '货物总价格',
	MONEY DECIMAL(19,3) NOT NULL COMMENT '应收金额',
	TOTAL_REBATE DECIMAL(19,4) NOT NULL COMMENT '定单总折扣',
	MIDDLE_RATE DECIMAL(19,4) COMMENT '总提点',
	MIDDLE_MONEY DECIMAL(19,3) COMMENT '中间人佣金',
	-- A  总金额 * 提点
	-- B  固定金额
	MIDDLE_CALC_TYPE VARCHAR(32) COMMENT '佣金计算方式 : A  总金额 * 提点
B  固定金额',
	MIDDLE_TOTAL DECIMAL(19,3) COMMENT '总佣金',
	EARNEST DECIMAL(19,3) COMMENT '订金',
	EARNEST_FIRST BIT(1) NOT NULL COMMENT '先付定金',
	RES_RECEIVED BIT(1) NOT NULL COMMENT '客户已收货',
	MONEY_COMPLETE BIT(1) NOT NULL COMMENT '完成收费',
	ARREARS BIT(1) COMMENT '欠款',
	ALL_STORE_OUT BIT(1) NOT NULL COMMENT '订单出库',
	CANCELED BIT(1) NOT NULL COMMENT '已取消',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '订单' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ORDER_FEE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	MONEY DECIMAL(19,3) NOT NULL COMMENT '金额',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	PAY BIT(1) NOT NULL COMMENT '已支付',
	CUSTOMER_ORDER VARCHAR(32) NOT NULL COMMENT '订单',
	PAY_TYPE VARCHAR(32) NOT NULL COMMENT '支付方式',
	CHECK_NUMBER VARCHAR(50) COMMENT '支票号',
	RECEIVE_NAME VARCHAR(50) NOT NULL COMMENT '收款人',
	BANK_NUMBER VARCHAR(50) COMMENT '银行帐号',
	BANK_INFO VARCHAR(100) COMMENT '开户行',
	MIDDLE_MONEY BIT(1) NOT NULL COMMENT '中间人费用',
	DEBIT VARCHAR(32) COMMENT '借方科目',
	CREDIT VARCHAR(32) COMMENT '贷方科目',
	RECEIVE_INFO VARCHAR(200) COMMENT '收款人信息',
	BANK VARCHAR(32) COMMENT '银行',
	PAY_DATE DATETIME COMMENT '支付时间',
	APPLY_DATE DATETIME NOT NULL COMMENT '申请时间',
	PAY_EMP VARCHAR(32) COMMENT '支付员工',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '订单费用' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.MIDDLE_MONEY_PAY
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	MIDDLE_MAN VARCHAR(32) NOT NULL COMMENT 'MIDDLE_MAN',
	CUSTOMER_ORDER VARCHAR(32) NOT NULL COMMENT '订单',
	MONEY DECIMAL(19,3) NOT NULL COMMENT '单价',
	OPER_DATE TIMESTAMP NOT NULL COMMENT '支付时间',
	OPER_EMP VARCHAR(32) NOT NULL COMMENT '操作人',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	PAY_TYPE VARCHAR(32) NOT NULL COMMENT '支付方式',
	CHECK_NUMBER VARCHAR(50) COMMENT '支票号',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '中间人佣金支付记录' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CUSTOMER_AREA
(
	ID VARCHAR(20) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	AREA_ROLE VARCHAR(32) NOT NULL COMMENT '销售角色',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '客户区' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.CUSTOMER
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	NAME VARCHAR(50) NOT NULL COMMENT '名称',
	-- 字典 代理商 施工方
	TYPE VARCHAR(32) NOT NULL COMMENT 'TYPE : 字典 代理商 施工方',
	MIDDLE_MAN VARCHAR(32) COMMENT 'MIDDLE_MAN',
	CONTACT VARCHAR(50) NOT NULL COMMENT '联系人',
	BALANCE DECIMAL(19,3) NOT NULL COMMENT '余额',
	VERSION INT COMMENT 'VERSION',
	TEL VARCHAR(50) NOT NULL COMMENT '电话',
	FAX VARCHAR(50) COMMENT '传真',
	MEMO VARCHAR(200) COMMENT '备注',
	MAIL VARCHAR(50) COMMENT 'MAIL',
	ENABLE BIT(1) NOT NULL COMMENT 'ENABLE',
	PROVINCE_CODE INT NOT NULL COMMENT '省市代码',
	LEVEL VARCHAR(32) NOT NULL COMMENT 'LEVEL',
	ADDRESS VARCHAR(200) NOT NULL COMMENT '地址',
	POST_CODE VARCHAR(10) NOT NULL COMMENT '邮编',
	CREATE_DATE DATETIME NOT NULL COMMENT '建立时间',
	CUSTOMER_AREA VARCHAR(20) NOT NULL COMMENT 'CUSTOMER_AREA',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '客户' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.ACCOUNT_OPER
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	OPER_EMP VARCHAR(32) NOT NULL COMMENT '操作人',
	OPER_MONEY DECIMAL(19,3) NOT NULL COMMENT '操作金额',
	OPER_TYPE VARCHAR(32) NOT NULL COMMENT '操作类型',
	OPER_DATE DATETIME NOT NULL COMMENT '操作日期',
	BEFOR_MONEY DECIMAL(19,3) NOT NULL COMMENT '操作前余额',
	AFTER_MONEY DECIMAL(19,3) NOT NULL COMMENT '操作后余额',
	CUSTOM VARCHAR(32) NOT NULL COMMENT 'CUSTOM',
	DESCRIPTION VARCHAR(200) COMMENT '描述',
	PAY_TYPE VARCHAR(32) NOT NULL COMMENT '支付方式',
	CHECK_NUMBER VARCHAR(50) COMMENT '支票号',
	DEBIT_ACCOUNT VARCHAR(32) COMMENT 'DEBIT_ACCOUNT',
	CREDIT_ACCOUNT VARCHAR(32) COMMENT 'CREDIT_ACCOUNT',
	CUSTOMER_ORDER VARCHAR(32) COMMENT 'CUSTOMER_ORDER',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '客户帐户操作记录' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.PREPARE_STOCK_CHANGE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	COUNT DECIMAL(19,4) NOT NULL COMMENT '件数',
	STOCK_CHANGE VARCHAR(32) NOT NULL COMMENT 'STOCK_CHANGE',
	STORE_RES VARCHAR(32) NOT NULL COMMENT 'STORE_RES',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '未审核入库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.PRODUCT_BACK_STORE_IN
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	CUSTOMER_ORDER VARCHAR(32) NOT NULL COMMENT '客户订单',
	STOCK_CNAHGE VARCHAR(32) COMMENT 'STOCK_CNAHGE',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '订单退货入库' DEFAULT CHARACTER SET utf8;


CREATE TABLE MINI_ERP.STOCK_CHANGE
(
	ID VARCHAR(32) NOT NULL COMMENT 'ID',
	STORE VARCHAR(32) NOT NULL COMMENT 'STORE',
	OPER_DATE DATETIME NOT NULL COMMENT '操作日期',
	OPER_EMP VARCHAR(32) NOT NULL COMMENT '操作人',
	OPER_TYPE VARCHAR(32) NOT NULL COMMENT '操作类型',
	MEMO VARCHAR(200) COMMENT '备注',
	VERIFY BIT(1) NOT NULL COMMENT 'VERIFY',
	PRIMARY KEY (ID)
) ENGINE = InnoDB COMMENT = '出入库' DEFAULT CHARACTER SET utf8;



/* Create Foreign Keys */

ALTER TABLE MINI_ERP.ALLOCATION_RES
	ADD FOREIGN KEY (ALLOCATION)
	REFERENCES MINI_ERP.ALLOCATION (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DEPOSITARY
	ADD FOREIGN KEY (STORE_AREA)
	REFERENCES MINI_ERP.STORE_AREA (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STORE_AREA
	ADD FOREIGN KEY (PARENT)
	REFERENCES MINI_ERP.STORE_AREA (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH
	ADD FOREIGN KEY (SUPPLIER)
	REFERENCES MINI_ERP.SUPPLIER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.SUPPLIER_RES
	ADD FOREIGN KEY (SUPPLIER)
	REFERENCES MINI_ERP.SUPPLIER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (STOCK_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE_ITEM (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH_STORE_COUNT
	ADD FOREIGN KEY (BATCH)
	REFERENCES MINI_ERP.BATCH (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM
	ADD FOREIGN KEY (BATCH)
	REFERENCES MINI_ERP.BATCH (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MATERIAL_STORE_OUT
	ADD FOREIGN KEY (PRODUCT_BATCH)
	REFERENCES MINI_ERP.BATCH (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH_ITEM
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.FORMAT
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PREPARE_STOCK_CHANGE
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ALLOCATION_RES
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_ITEM
	ADD FOREIGN KEY (STORE_RES)
	REFERENCES MINI_ERP.STORE_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.FORMAT
	ADD FOREIGN KEY (DEFINE)
	REFERENCES MINI_ERP.FORMAT_DEFINE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_ITEM
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STORE_RES
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.SUPPLIER_RES
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.FORMAT_DEFINE
	ADD FOREIGN KEY (RES)
	REFERENCES MINI_ERP.RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES_CATEGORY
	ADD FOREIGN KEY (CATEGORY)
	REFERENCES MINI_ERP.RES_CATEGORY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (CATEGORY)
	REFERENCES MINI_ERP.RES_CATEGORY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES_UNIT
	ADD FOREIGN KEY (UNIT_GROUP)
	REFERENCES MINI_ERP.UNIT_GROUP (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (UNIT_GROUP)
	REFERENCES MINI_ERP.UNIT_GROUP (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (RES_UNIT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (IN_DEFAULT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (MASTER_UNIT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_ITEM
	ADD FOREIGN KEY (MONEY_UNIT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_ITEM
	ADD FOREIGN KEY (MIDDLE_UNIT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (OUT_DEFAULT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH_ITEM
	ADD FOREIGN KEY (COUNT_UNIT)
	REFERENCES MINI_ERP.RES_UNIT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.CUSTOMER
	ADD FOREIGN KEY (LEVEL)
	REFERENCES MINI_ERP.CUSTOMER_LEVEL (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.RES
	ADD FOREIGN KEY (ACCOUNTING)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_FEE
	ADD FOREIGN KEY (DEBIT)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ACCOUNT_OPER
	ADD FOREIGN KEY (CREDIT_ACCOUNT)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_FEE
	ADD FOREIGN KEY (CREDIT)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ACCOUNTING
	ADD FOREIGN KEY (PARENT)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ACCOUNT_OPER
	ADD FOREIGN KEY (DEBIT_ACCOUNT)
	REFERENCES MINI_ERP.ACCOUNTING (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (PRODUCT_TO_DOOR)
	REFERENCES MINI_ERP.PRODUCT_TO_DOOR (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (EXPRESS_INFO)
	REFERENCES MINI_ERP.EXPRESS_INFO (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (EXPRESS_CAR)
	REFERENCES MINI_ERP.EXPRESS_CAR (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.EXPRESS_CAR
	ADD FOREIGN KEY (DRIVER)
	REFERENCES MINI_ERP.EXPRESS_DRIVER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.EXPRESS_INFO
	ADD FOREIGN KEY (TRANS)
	REFERENCES MINI_ERP.TRANS_CORP (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PRODUCT_TO_DOOR
	ADD FOREIGN KEY (CAR)
	REFERENCES MINI_ERP.CARS (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (DEPOSITARY)
	REFERENCES MINI_ERP.DEPOSITARY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH_AND_AREA
	ADD FOREIGN KEY (STORE_AREA)
	REFERENCES MINI_ERP.DEPOSITARY (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (BATCH_STORE_COUNT)
	REFERENCES MINI_ERP.BATCH_STORE_COUNT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH_AND_AREA
	ADD FOREIGN KEY (BATCH)
	REFERENCES MINI_ERP.BATCH_STORE_COUNT (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DEPOSITARY
	ADD FOREIGN KEY (STOCK)
	REFERENCES MINI_ERP.STOCK (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM
	ADD FOREIGN KEY (STOCK)
	REFERENCES MINI_ERP.STOCK (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (STOCK)
	REFERENCES MINI_ERP.STOCK (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BATCH_STORE_COUNT
	ADD FOREIGN KEY (STOCK)
	REFERENCES MINI_ERP.STOCK (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PRODUCT_BACK_STORE_IN
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.ORDER_BACK (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STORE_AREA
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ALLOCATION
	ADD FOREIGN KEY (APPLY_STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.INVENTORY
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ALLOCATION
	ADD FOREIGN KEY (TARGET_STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PRODUCT_BACK_STORE_IN
	ADD FOREIGN KEY (STORE)
	REFERENCES MINI_ERP.STORE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.CUSTOMER
	ADD FOREIGN KEY (MIDDLE_MAN)
	REFERENCES MINI_ERP.MIDDLE_MAN (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MIDDLE_MONEY_PAY
	ADD FOREIGN KEY (MIDDLE_MAN)
	REFERENCES MINI_ERP.MIDDLE_MAN (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (NEED_RES)
	REFERENCES MINI_ERP.NEED_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_ITEM
	ADD FOREIGN KEY (NEED_RES)
	REFERENCES MINI_ERP.NEED_RES (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH_ITEM
	ADD FOREIGN KEY (DISPATCH)
	REFERENCES MINI_ERP.DISPATCH (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_BACK
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.CUSTOMER_ORDER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_FEE
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.CUSTOMER_ORDER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MIDDLE_MONEY_PAY
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.CUSTOMER_ORDER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NEED_RES
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.CUSTOMER_ORDER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ACCOUNT_OPER
	ADD FOREIGN KEY (CUSTOMER_ORDER)
	REFERENCES MINI_ERP.CUSTOMER_ORDER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.CUSTOMER
	ADD FOREIGN KEY (CUSTOMER_AREA)
	REFERENCES MINI_ERP.CUSTOMER_AREA (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ACCOUNT_OPER
	ADD FOREIGN KEY (CUSTOM)
	REFERENCES MINI_ERP.CUSTOMER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.CUSTOMER_ORDER
	ADD FOREIGN KEY (CUSTOMER_ID)
	REFERENCES MINI_ERP.CUSTOMER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.BACK_PREPARE_MONEY
	ADD FOREIGN KEY (ACCOUNT_OPER)
	REFERENCES MINI_ERP.ACCOUNT_OPER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PREPARE_PAY
	ADD FOREIGN KEY (ACCOUNT_OPER)
	REFERENCES MINI_ERP.ACCOUNT_OPER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ORDER_BACK
	ADD FOREIGN KEY (BACK_MONEY)
	REFERENCES MINI_ERP.ACCOUNT_OPER (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.NO_CONVERT_COUNT
	ADD FOREIGN KEY (PREPARE_CHANGE)
	REFERENCES MINI_ERP.PREPARE_STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PREPARE_STOCK_CHANGE
	ADD FOREIGN KEY (STOCK_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ASSEMBLY
	ADD FOREIGN KEY (STORE_IN)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PRODUCT_BACK_STORE_IN
	ADD FOREIGN KEY (STOCK_CNAHGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STOCK_CHANGE_ITEM
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.INVENTORY
	ADD FOREIGN KEY (LOSS_RES)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.INVENTORY
	ADD FOREIGN KEY (ADD_RES)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ALLOCATION
	ADD FOREIGN KEY (STORE_OUT)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ALLOCATION
	ADD FOREIGN KEY (STORE_IN)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.PRODUCT_STORE_IN
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.SCRAP_STORE_OUT
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.STORE_CHANGE
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MATERIAL_STORE_OUT
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MATERIAL_BACK_STORE_IN
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.MATERIAL_STORE_IN
	ADD FOREIGN KEY (STORE_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ASSEMBLY
	ADD FOREIGN KEY (LOSE_OUT)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.DISPATCH
	ADD FOREIGN KEY (STOCK_CHANGE)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


ALTER TABLE MINI_ERP.ASSEMBLY
	ADD FOREIGN KEY (STORE_OUT)
	REFERENCES MINI_ERP.STOCK_CHANGE (ID)
	ON UPDATE RESTRICT
	ON DELETE RESTRICT
;


