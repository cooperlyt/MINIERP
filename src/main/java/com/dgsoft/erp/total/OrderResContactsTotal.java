package com.dgsoft.erp.total;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.Name;

import java.util.Arrays;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/03/14
 * Time: 12:15
 */
@Name("orderResContactsTotal")
public class OrderResContactsTotal extends ErpEntityQuery<OrderItem>{

    protected static final String EJBQL = "select orderItem from OrderItem orderItem " +
            "left join fetch orderItem.dispatch dispatch left join fetch dispatch.stockChange stockChange " +
            "left join fetch orderItem.storeRes storeRes left join fetch storeRes.res res " +
            "left join fetch res.unitGroup unitGroup left join fetch dispatch.stockChange " +
            "left join fetch orderItem.needRes needRes left join fetch needRes.customerOrder customerOrder " +
            "left join fetch customerOrder.customer customer left join fetch customer.customerArea " +

            "left join fetch customer.customerLevel where orderItem.status in ('WAIT_PRICE', 'COMPLETED')";

    protected static final String[] RESTRICTIONS = {
            "orderItem.dispatch.stockChange.operDate >= #{searchDateArea.dateFrom}",
            "orderItem.dispatch.stockChange.operDate <= #{searchDateArea.searchDateTo}",
            "orderItem.needRes.customerOrder.customer.customerArea.id = #{customerSearchCondition.customerAreaId}",
            "orderItem.needRes.customerOrder.customer.customerLevel.priority >= #{customerSearchCondition.levelFrom}",
            "orderItem.needRes.customerOrder.customer.customerLevel.priority <= #{customerSearchCondition.levelTo}",
            "orderItem.needRes.customerOrder.customer.type = #{customerSearchCondition.type}",
            "orderItem.needRes.customerOrder.customer.provinceCode <= #{customerSearchCondition.provinceCode}",
            "orderItem.presentation = #{customerResCondition.freeCondition}"
    };

    public OrderResContactsTotal() {
        setEjbql(EJBQL);
        setRestrictionLogicOperator("and");
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setOrderColumn("orderItem.dispatch.stockChange.operDate");
    }
}
