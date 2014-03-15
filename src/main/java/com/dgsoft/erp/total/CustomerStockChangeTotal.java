package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Customer;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResCountTotalGroup;

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
                    return ((StockChangeItem) stockChangeItem).getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
                }

                @Override
                public Object totalGroupData(Collection<StoreResCountEntity> datas) {
                    return null;
                }
            }, StoreResGroupStrategy.getInstance());

            TotalDataGroup.unionData(customerResultGroup,StoreResCountUnionStrategy.getInstance());

            TotalDataGroup.sort(customerResultGroup, new Comparator<StoreResCountEntity>() {
                @Override
                public int compare(StoreResCountEntity o1, StoreResCountEntity o2) {
                    return 0;
                }
            });

        }
    }

    public TotalDataGroup<?, StoreResCountEntity> getCustomerResultGroup() {
        if ( isAnyParameterDirty() )
        {
            refresh();
        }
        initCustomerGroupResultList();
        return customerResultGroup;
    }


    @Override
    public void refresh(){
        super.refresh();
        customerResultGroup = null;
    }

    @Deprecated
    public Map<Customer, StoreResCountTotalGroup> getCustomerTotalResultMap() {

        Map<Customer, StoreResCountTotalGroup> result = new HashMap<Customer, StoreResCountTotalGroup>();
        for (StockChangeItem item : getResultList()) {
            Customer customer = item.getStockChange().getOrderDispatch().getNeedRes().getCustomerOrder().getCustomer();
            StoreResCountTotalGroup mapValue = result.get(customer);
            if (mapValue == null) {
                mapValue = new StoreResCountTotalGroup();
                result.put(customer, mapValue);
            }
            mapValue.put(item);
        }

        return result;
    }
}
