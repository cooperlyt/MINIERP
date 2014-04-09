package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCountEntity;

import java.util.*;

/**
 * Created by cooper on 3/15/14.
 */
public abstract class CustomerStockChangeTotal extends StoreChangeResTotal {


    protected static final String EJBQL = "select stockChangeItem from StockChangeItem stockChangeItem left join fetch stockChangeItem.stockChange sc left join fetch sc.orderDispatch od left join fetch od.needRes nr left join fetch nr.customerOrder co left join fetch co.customer customer where stockChangeItem.stockChange.verify = true";

    public CustomerStockChangeTotal() {
        super();
        setEjbql(EJBQL);
    }

    private TotalDataGroup<?, StoreResCountEntity> customerResultGroup = null;

    private void initCustomerGroupResultList() {
        if (customerResultGroup == null) {
            customerResultGroup = TotalDataGroup.allGroupBy(new ArrayList<StoreResCountEntity>(getResultList()), new TotalGroupStrategy<Customer, StoreResCountEntity>() {
                @Override
                public Customer getKey(StoreResCountEntity stockChangeItem) {
                    if (((StockChangeItem) stockChangeItem).getStockChange().getOperType().equals(StockChange.StoreChangeType.SELL_OUT)) {
                        return ((StockChangeItem) stockChangeItem).getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();

                    } else {
                        return ((StockChangeItem) stockChangeItem).getStockChange().getBACKDISPATCH().getOrderBack().getCustomer();
                    }
                }

                @Override
                public Object totalGroupData(Collection<StoreResCountEntity> datas) {
                    return null;
                }
            }, new StoreResGroupStrategy<StoreResCountEntity>());

            TotalDataGroup.unionData(customerResultGroup,new StoreResCountUnionStrategy<StoreResCountEntity>());

            TotalDataGroup.sort(customerResultGroup, new Comparator<StoreResCountEntity>() {
                @Override
                public int compare(StoreResCountEntity o1, StoreResCountEntity o2) {
                    return 0;
                }
            });

        }
    }

    public TotalDataGroup<?, StoreResCountEntity> getCustomerResultGroup() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initCustomerGroupResultList();
        return customerResultGroup;
    }

    private TotalDataGroup<?, StockChangeItem> customerDetailsResultGroup = null;

    private void initCustomerDetailsResultGroup() {
        if (customerDetailsResultGroup == null) {
            customerDetailsResultGroup = TotalDataGroup.allGroupBy(getResultList(), new TotalGroupStrategy<Customer, StockChangeItem>() {
                @Override
                public Customer getKey(StockChangeItem stockChangeItem) {
                    if (stockChangeItem.getStockChange().getOperType().equals(StockChange.StoreChangeType.SELL_OUT)) {
                        return stockChangeItem.getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
                    } else {
                        return stockChangeItem.getStockChange().getBACKDISPATCH().getOrderBack().getCustomer();
                    }
                }

                @Override
                public Object totalGroupData(Collection<StockChangeItem> datas) {
                    return null;
                }
            });
        }

    }

    public TotalDataGroup<?, StockChangeItem> getCustomerDetailsResultGroup() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initCustomerDetailsResultGroup();
        return customerDetailsResultGroup;
    }


    @Override
    public void refresh() {
        super.refresh();
        customerResultGroup = null;
        customerDetailsResultGroup = null;
    }

//    @Deprecated
//    public Map<Customer, StoreResCountTotalGroup> getCustomerTotalResultMap() {
//
//        Map<Customer, StoreResCountTotalGroup> result = new HashMap<Customer, StoreResCountTotalGroup>();
//        for (StockChangeItem item : getResultList()) {
//            Customer customer = item.getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
//            StoreResCountTotalGroup mapValue = result.get(customer);
//            if (mapValue == null) {
//                mapValue = new StoreResCountTotalGroup();
//                result.put(customer, mapValue);
//            }
//            mapValue.put(item);
//        }
//
//        return result;
//    }
}
