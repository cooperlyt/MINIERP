package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerArea;
import com.dgsoft.erp.tools.OrderStateCondition;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import javax.persistence.EntityManager;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 31/03/14
 * Time: 16:44
 */
@Name("saleMoneySum")
public class SaleMoneySum {


    @In(create = true)
    private EntityManager erpEntityManager;

    private OrderStateCondition orderState = new OrderStateCondition();

    private SearchDateArea dateArea = new SearchDateArea(new Date(), new Date());

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


    public void totalData() {
        genPrepareMoney();
        genOrderPayMoneySum();
        genOrderMoneySum();


        genAreaPrepareMoney();
        genAreaOrderPayMoney();
        genAreaOrderMoneySum();

        List<CustomerArea> areas = erpEntityManager.createQuery("select area from CustomerArea area ", CustomerArea.class).getResultList();
        if (areas.isEmpty()) {
            areaPrepareMoney = null;
            areaOrderPayMoney = null;
            areaOrderMoneySum = null;
        }
        for (CustomerArea area: areas){
            if (areaPrepareMoney.get(area.getName()) == null){
                areaPrepareMoney.put(area.getName(),new OrderMoneySeries());
            }
            if (areaOrderPayMoney.get(area.getName()) == null){
                areaOrderPayMoney.put(area.getName(),new OrderMoneySeries());
            }
            if (areaOrderMoneySum.get(area.getName()) == null){
                areaOrderMoneySum.put(area.getName(),new OrderMoneySeries());
            }
        }

    }

    public Map<String, OrderMoneySeries> areaOrderMoneySum = null;

    public void genAreaOrderMoneySum() {
        List<OrderMoneySeries> list = dateArea.setQueryParam(orderState.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(o.customer.customerArea.name,sum(o.money),count(o.id) ) from CustomerOrder o where " + dateArea.genConditionSQL("o.createDate", false) + orderState.genConditionSQL("o", true) + " group by o.customer.customerArea.name", OrderMoneySeries.class))).getResultList();
        areaOrderMoneySum = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : list) {
            areaOrderMoneySum.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> areaOrderPayMoney = null;

    public void genAreaOrderPayMoney() {
        List<OrderMoneySeries> result = dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(accountOper.customer.customerArea.name,sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.customerOrder.createDate", true) + " group by accountOper.customer.customerArea.name", OrderMoneySeries.class)).setParameter("operType", AccountOper.AccountOperType.ORDER_SAVINGS).getResultList();
        areaOrderPayMoney = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : result) {
            areaOrderPayMoney.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> areaPrepareMoney = null;

    public void genAreaPrepareMoney() {
        List<OrderMoneySeries> result = dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(accountOper.customer.customerArea.name,sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.operDate", true) + " group by accountOper.customer.customerArea.name", OrderMoneySeries.class)).setParameter("operType", AccountOper.AccountOperType.PRE_DEPOSIT).getResultList();
        areaPrepareMoney = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : result) {
            areaPrepareMoney.put(series.getKey(), series);
        }
    }


    private OrderMoneySeries orderMoneySum = null;

    public void genOrderMoneySum() {
        orderMoneySum = (OrderMoneySeries) dateArea.setQueryParam(orderState.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(o.money),count(o.id) ) from CustomerOrder o where " + dateArea.genConditionSQL("o.createDate", false) + orderState.genConditionSQL("o", true)))).getSingleResult();
        if (orderMoneySum == null) {
            orderMoneySum = new OrderMoneySeries();
        }

    }

    private OrderMoneySeries orderPayMoneySum = null;

    public void genOrderPayMoneySum() {
        orderPayMoneySum = (OrderMoneySeries) dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.customerOrder.createDate", true))).setParameter("operType", AccountOper.AccountOperType.ORDER_SAVINGS).getSingleResult();
        if (orderPayMoneySum == null) {
            orderPayMoneySum = new OrderMoneySeries();
        }
    }

    private OrderMoneySeries prepareMoney = null;

    public void genPrepareMoney() {
        prepareMoney = (OrderMoneySeries) dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.operDate", true))).setParameter("operType", AccountOper.AccountOperType.PRE_DEPOSIT).getSingleResult();
        if (prepareMoney == null) {
            prepareMoney = new OrderMoneySeries();
        }
    }

    public Map<String, OrderMoneySeries> getAreaOrderMoneySum() {
        return areaOrderMoneySum;
    }

    public Map<String, OrderMoneySeries> getAreaOrderPayMoney() {
        return areaOrderPayMoney;
    }

    public Map<String, OrderMoneySeries> getAreaPrepareMoney() {
        return areaPrepareMoney;
    }

    public OrderMoneySeries getOrderMoneySum() {
        return orderMoneySum;
    }

    public OrderMoneySeries getOrderPayMoneySum() {
        return orderPayMoneySum;
    }

    public OrderMoneySeries getPrepareMoney() {
        return prepareMoney;
    }
}
