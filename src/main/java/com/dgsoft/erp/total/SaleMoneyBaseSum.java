package com.dgsoft.erp.total;

import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.Name;

import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 31/03/14
 * Time: 16:44
 */
@Name("saleMoneySum")
public class SaleMoneyBaseSum extends SaleDayBaseSum {



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


    public Map<String, OrderMoneySeries> getAreaOrderPayMoney() {
        return areaOrderPayMoney;
    }

    public Map<String, OrderMoneySeries> getAreaPrepareMoney() {
        return areaPrepareMoney;
    }



    public OrderMoneySeries getOrderPayMoneySum() {
        return orderPayMoneySum;
    }

    public OrderMoneySeries getPrepareMoney() {
        return prepareMoney;
    }
}
