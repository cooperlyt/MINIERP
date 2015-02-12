package com.dgsoft.erp.total;

import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.api.StoreResPriceEntity;

import java.util.Comparator;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-5-23
 * Time: 下午1:08
 */
public class SaleBackItemComparator implements Comparator<StoreResPriceEntity> {

    @Override
    public int compare(StoreResPriceEntity o1, StoreResPriceEntity o2) {
        Date o1Date;
        if (o1 instanceof OrderItem) {
            o1Date = ((OrderItem) o1).getNeedRes().getCustomerOrder().getCreateDate();
        } else {
            o1Date = ((BackItem) o1).getDispatch().getStockChange().getOperDate();
        }

        Date o2Date;
        if (o2 instanceof OrderItem) {
            o2Date = ((OrderItem) o2).getNeedRes().getCustomerOrder().getCreateDate();
        } else {
            o2Date = ((BackItem) o2).getDispatch().getStockChange().getOperDate();
        }
        int result = o1Date.compareTo(o2Date);
        if (result == 0) {

            String o1CId;

            if (o1 instanceof OrderItem) {
                o1CId = ((OrderItem) o1).getNeedRes().getCustomerOrder().getCustomer().getId();
            } else {
                o1CId = ((BackItem) o1).getOrderBack().getCustomer().getId();
            }

            String o2CId;
            if (o2 instanceof OrderItem) {
                o2CId = ((OrderItem) o2).getNeedRes().getCustomerOrder().getCustomer().getId();
            } else {
                o2CId = ((BackItem) o2).getOrderBack().getCustomer().getId();
            }

            result = o1CId.compareTo(o2CId);

            if (result == 0) {

                result = o1.getRes().compareTo(o2.getRes());

                if (result == 0) {
                    result = o1.getStoreRes().compareTo(o2.getStoreRes());
                }

            }

        }
        return result;
    }
}
