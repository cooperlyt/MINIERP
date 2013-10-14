-- If you are using Hibernate as the JPA provider, you can use this file to load seed data into the database using SQL statements
-- The portable approach is to use a startup component (such as the @PostConstruct method of a @Startup @Singleton) or observe a lifecycle event fired by Seam Servlet

-- 系统参数
INSERT INTO DG_SYSTEM.SYSTEM_PARAM(ID,TYPE,VALUE,MEMO) VALUES('erp.autoGenerateStoreInCode','BOOLEAN','true','是否自动生成入库单编号,是:true');
INSERT INTO DG_SYSTEM.SYSTEM_PARAM(ID,TYPE,VALUE,MEMO) VALUES('erp.autoGenerateStoreOutCode','BOOLEAN','true','是否自动生成出库单编号,是:true');
-- 功能种类
INSERT INTO DG_SYSTEM.FUNC_CATEGORY (ID, NAME, ICON, PRIORITY, MEMO) VALUES ('system.config', '系统设置', '', '1', '超级管理员,一般由实施方有此权限');
INSERT INTO DG_SYSTEM.FUNC_CATEGORY (ID, NAME, ICON, PRIORITY, MEMO) VALUES ('system.manager', '系统管理', '', '2', '管理员');
INSERT INTO DG_SYSTEM.FUNC_CATEGORY (ID, NAME, ICON, PRIORITY, MEMO) VALUES ('erp.store','仓库','','3','');

-- 功能
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.param', '系统参数管理', 'system.config', '', '/func/system/config/SystemParams.seam', '', '2', '系统运行方式设置');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.person', '人员维护', 'system.manager', '', '/func/system/manager/PersonMgr.seam', '', '3', '管理自然人');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.businessConfig','业务管理','system.config','','/func/system/config/BusinessMgr.seam','','3','业务处理配置');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.employee','员工管理','system.manager', '', '/func/system/manager/EmployeeMgr.seam','','4','员工和组织机构管理');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.role','角色管理','system.config','','/func/system/config/RoleMgr.seam','','4','角色管理和角色分配启动业务');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.word','字典管理','system.manager','','/func/system/manager/WordMgr.seam','','5','字典管理');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('system.roleCategory','角色组管理','system.config','','/func/system/config/RoleCategoryMgr.seam','','6','角色组');


INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.data.res','物资管理','system.manager','','/func/erp/data/ResMgr.seam','','10','物资和物资种类设置');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.data.store','仓库管理','system.manager','','/func/erp/data/StoreMgr.seam','','11','仓库管理及仓库权限分配');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.store.area','仓库区设置','erp.store','','/func/erp/store/StoreAreaMgr.seam','','1','仓库存储区域设置');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.store.in','入库','erp.store','','/func/erp/store/StoreIn.seam','','2','物资入库');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.store.out','出库','erp.store','','/func/erp/store/StoreOut.seam','','3','物资出库');
INSERT INTO DG_SYSTEM.FUNCTION (ID, NAME, CATEGORY, ICON, LOCATION, BANNER, PRIORITY, MEMO) VALUES ('erp.store.inventorySearch','库存查询','erp.store','','/func/erp/store/InventorySearch.seam','','4','库存查询');

-- 角色
INSERT INTO DG_SYSTEM.ROLE (ID, NAME, ROLE_TYPE, DESCRIPTION) VALUES ('system.config', '系统设置', 'ROLE_CATEGORY', '系统配置');
INSERT INTO DG_SYSTEM.ROLE (ID, NAME, ROLE_TYPE, DESCRIPTION) VALUES ('system.manager', '系统管理','ROLE_CATEGORY', '系统管理');

INSERT INTO DG_SYSTEM.ROLE (ID, NAME, ROLE_TYPE, DESCRIPTION) VALUES ('erp.data.manager','数据管理员','ROLE_CATEGORY','ERP基础数据管理');
INSERT INTO DG_SYSTEM.ROLE (ID, NAME, ROLE_TYPE, DESCRIPTION) VALUES ('erp.store.manager','仓库管理员','ROLE_CATEGORY','仓库');


-- ROLE_CATEGORY
INSERT INTO DG_SYSTEM.ROLE_CATEGORY (ID, NAME, PRIORITY, DESCRIPTION,SYSTEM) VALUES ('superAdmin', '超级管理员','1', '一般为系统实施人员',b'1');
INSERT INTO DG_SYSTEM.ROLE_CATEGORY (ID,NAME, PRIORITY, DESCRIPTION,SYSTEM) VALUES ('admin','管理员','2','系统管理员',b'1');

INSERT INTO DG_SYSTEM.ROLE_CATEGORY (ID,NAME, PRIORITY, DESCRIPTION,SYSTEM) VALUES ('storeMgr','仓库管理员','3','仓库管理员',b'1');


-- ROLE_FUNCTION 角色种类
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin', 'system.param');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin', 'system.businessConfig');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin', 'system.employee');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin', 'system.person');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin', 'system.role');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('superAdmin','system.roleCategory');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('admin', 'system.employee');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('admin', 'system.person');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('admin', 'system.word');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('admin', 'erp.data.res');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('admin', 'erp.data.store');

INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('storeMgr', 'erp.store.area');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('storeMgr', 'erp.store.in');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('storeMgr', 'erp.store.out');
INSERT INTO DG_SYSTEM.ROLE_FUNCTION (ROL_CATE_ID, FUN_ID) VALUES ('storeMgr', 'erp.store.inventorySearch');

-- ROLE_ROLE_CATEGROY
INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY (ROLE_ID, CAT_ID) VALUES ('system.config', 'superAdmin');
INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY (ROLE_ID, CAT_ID) VALUES ('system.manager', 'superAdmin');

INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY (ROLE_ID, CAT_ID) VALUES ('system.manager', 'admin');

INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY (ROLE_ID, CAT_ID) VALUES ('erp.data.manager', 'admin');

INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY (ROLE_ID, CAT_ID) VALUES ('erp.store.manager', 'storeMgr');


--
-- ADMIN INSERY
INSERT INTO DG_SYSTEM.PERSON (ID,NAME,CREDENTIALS_TYPE,_FOREIGN,CREDENTIALS_NUMBER,DATE_OF_BIRTH) VALUES ('admin','admin','OTHER',1,'1','2013-07-15 10:27:08');
-- INSERT INTO DG_SYSTEM.EMPLOYEE(ID,ENABLE,PERSON_ID,PASSWORD,ORGANIZATION) VALUES ('admin',b'1','admin','admin','0');
-- INSERT INTO DG_SYSTEM.ROLE_CATE_EMP (EMP_ID, ROLE_ID) VALUES ('admin','admin');
-- INSERT INTO DG_SYSTEM.ROLE_ROLE_CATEGROY(ROLE_ID, CAT_ID) VALUES ('system.manager','admin');



-- WORD

INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('system.empJob','职位','',b'1');

INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('system.empJob.manager','manager','经理','system.empJob','','3',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('system.empJob.boss','boss','总经理','system.empJob','','5',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('system.empJob.factoryManager','factoryManager','厂长','system.empJob','','4',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('system.empJob.emp','emp','职员','system.empJob','','2',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('system.empJob.other','ohter','其它','system.empJob','','10',b'1');

INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('erp.storeInReason','入库原因','',b'1');

INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeInReason.product','product','生产入库','erp.storeInReason','','1',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeInReason.material','material','源材料采购入库','erp.storeInReason','','2',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeInReason.storeSwitch','storeSwitch','移库入库','erp.storeInReason','','3',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeInReason.other','other','其它原因','erp.storeInReason','','4',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeInReason.orderBack','orderBack','退单入库','erp.storeInReason','','5',b'0');


INSERT INTO DG_SYSTEM.WORD_CATEGORY(ID, NAME, MEMO, SYSTEM) VALUES ('erp.storeOutReason','出库原因','',b'1');

INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeOutReason.product','product','产品再加工出库','erp.storeOutReason','','2',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeOutReason.material','material','生产源料出库','erp.storeOutReason','','1',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeOutReason.storeSwitch','storeSwitch','移库出库','erp.storeOutReason','','3',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeOutReason.other','other','其它原因','erp.storeOutReason','','4',b'1');
INSERT INTO DG_SYSTEM.WORD(ID, _KEY, _VALUE, CATEGORY, DESCRIPTION, PRIORITY, ENABLE) VALUES ('erp.storeOutReason.orderBack','orderBack','订单出库','erp.storeOutReason','','5',b'0');