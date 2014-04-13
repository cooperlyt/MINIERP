package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created by cooper on 3/2/14.
 */

@Name("customerResConfirm")
public class CustomerResConfirm extends ErpEntityQuery<OrderItem> {

    protected static final String EJBQL = "select orderItem from OrderItem orderItem";

    private static final String[] RESTRICTIONS = {
            "orderItem.status in (#{customerResConfirm.status})",
            "orderItem.dispatch.stockChange.operDate >= #{customerResConfirm.searchDateArea.dateFrom}",
            "orderItem.dispatch.stockChange.operDate <= #{customerResConfirm.searchDateArea.searchDateTo}",
            "orderItem.needRes.customerOrder.customer.id = #{customerResConfirm.coustomerId}"};

    public CustomerResConfirm() {
        setEjbql(EJBQL);
        setRestrictionLogicOperator("and");
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setOrderColumn("orderItem.dispatch.stockChange.operDate");
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(), new Date());

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }


    public List<OrderItem.OrderItemStatus> getStatus(){
        return new ArrayList<OrderItem.OrderItemStatus>(EnumSet.of(OrderItem.OrderItemStatus.WAIT_PRICE, OrderItem.OrderItemStatus.COMPLETED));
    }

    private String coustomerId;

    public String getCoustomerId() {
        return coustomerId;
    }

    public void setCoustomerId(String coustomerId) {
        this.coustomerId = coustomerId;
    }

}
