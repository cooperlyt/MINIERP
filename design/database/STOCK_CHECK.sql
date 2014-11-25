select stock.ID, stock.COUNT, (
                                (SELECT
                              ifnull(sum(oi.COUNT),0)
                                 FROM MINI_ERP.STOCK_CHANGE_ITEM oi LEFT JOIN MINI_ERP.STOCK_CHANGE oic ON oic.ID = oi.STORE_CHANGE
                                 WHERE oi.STOCK = stock.id AND oic.OPER_TYPE IN
                                                               ('MATERIAL_IN', 'MATERIAL_BACK_IN', 'SELL_BACK', 'PRODUCE_IN', 'ALLOCATION_IN', 'ASSEMBLY_IN', 'STORE_CHECK_ADD', 'STORE_CHANGE_IN'))
                                -
                                (SELECT
                              ifnull(sum(oo.COUNT),0)
                                 FROM MINI_ERP.STOCK_CHANGE_ITEM oo LEFT JOIN MINI_ERP.STOCK_CHANGE ooc ON ooc.ID = oo.STORE_CHANGE
                                 WHERE oo.STOCK = stock.id AND ooc.OPER_TYPE IN
                                                               ('MATERIAL_OUT', 'SELL_OUT', 'ALLOCATION_OUT', 'ASSEMBLY_OUT', 'SCRAP_OUT', 'STORE_CHECK_LOSS', 'STORE_CHANGE_OUT'))
) rc from MINI_ERP.STOCK stock WHERE  stock.COUNT <> (
  (SELECT
     ifnull(sum(oi.COUNT),0)
   FROM MINI_ERP.STOCK_CHANGE_ITEM oi LEFT JOIN MINI_ERP.STOCK_CHANGE oic ON oic.ID = oi.STORE_CHANGE
   WHERE oi.STOCK = stock.id AND oic.OPER_TYPE IN
                                    ('MATERIAL_IN', 'MATERIAL_BACK_IN', 'SELL_BACK', 'PRODUCE_IN', 'ALLOCATION_IN', 'ASSEMBLY_IN', 'STORE_CHECK_ADD', 'STORE_CHANGE_IN'))
  -
  (SELECT
     ifnull(sum(oo.COUNT),0)
   FROM MINI_ERP.STOCK_CHANGE_ITEM oo LEFT JOIN MINI_ERP.STOCK_CHANGE ooc ON ooc.ID = oo.STORE_CHANGE
   WHERE oo.STOCK = stock.id AND ooc.OPER_TYPE IN
                                    ('MATERIAL_OUT', 'SELL_OUT', 'ALLOCATION_OUT', 'ASSEMBLY_OUT', 'SCRAP_OUT', 'STORE_CHECK_LOSS', 'STORE_CHANGE_OUT'))
);


select count(stock.id) from MINI_ERP.STOCK stock