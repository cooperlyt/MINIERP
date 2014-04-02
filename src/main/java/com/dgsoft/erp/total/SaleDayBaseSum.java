package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.tools.OrderStateCondition;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 02/04/14
 * Time: 09:03
 */

public abstract class SaleDayBaseSum {

    @In(create = true)
    protected EntityManager erpEntityManager;

    protected OrderStateCondition orderState = new OrderStateCondition();

    protected SearchDateArea dateArea = new SearchDateArea(new Date(), new Date());

    public OrderStateCondition getOrderState() {
        return orderState;
    }

    public void setOrderState(OrderStateCondition orderState) {
        this.orderState = orderState;
    }

    public SearchDateArea getDateArea() {
        return dateArea;
    }

    public void setDateArea(SearchDateArea dateArea) {
        this.dateArea = dateArea;
    }

    protected OrderMoneySeries orderMoneySum = null;

    public void genOrderMoneySum() {
        orderMoneySum = (OrderMoneySeries) dateArea.setQueryParam(orderState.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(o.money),count(o.id) ) from CustomerOrder o where " + dateArea.genConditionSQL("o.createDate", false) + orderState.genConditionSQL("o", true)))).getSingleResult();
        if (orderMoneySum == null) {
            orderMoneySum = new OrderMoneySeries();
        }

    }

    public OrderMoneySeries getOrderMoneySum() {
        return orderMoneySum;
    }


    protected Map<String, OrderMoneySeries> areaOrderMoneySum = null;

    public void genAreaOrderMoneySum() {
        List<OrderMoneySeries> list = dateArea.setQueryParam(orderState.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(o.customer.customerArea.name,sum(o.money),count(o.id) ) from CustomerOrder o where " + dateArea.genConditionSQL("o.createDate", false) + orderState.genConditionSQL("o", true) + " group by o.customer.customerArea.name", OrderMoneySeries.class))).getResultList();
        areaOrderMoneySum = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : list) {
            areaOrderMoneySum.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> getAreaOrderMoneySum() {
        return areaOrderMoneySum;
    }

}
