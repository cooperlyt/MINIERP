package com.dgsoft.erp.total;

import com.dgsoft.erp.model.StockChange;

/**
 * Created by cooper on 3/15/14.
 */
public class CustomerResSellTotal extends CustomerStockChangeTotal {


    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem left join fetch stockChangeItem.stockChange sc left join fetch sc.orderDispatch od left join fetch od.needRes nr left join fetch nr.customerOrder co left join fetch co.customer customer where stockChangeItem.stockChange.operType = \'SELL_OUT\' and stockChangeItem.stockChange.verify = true";


    public CustomerResSellTotal() {
        super();
        setEjbql(EJBQL);
    }

}
