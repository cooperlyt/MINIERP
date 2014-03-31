package com.dgsoft.erp.total;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.AccountOper;
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

    private OrderMoneySeries orderMoneySum = null;

    public OrderMoneySeries getOrderMoneySum() {
        if (orderMoneySum == null) {
            orderMoneySum = (OrderMoneySeries) dateArea.setQueryParam(orderState.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(o.money),count(o.id) ) from CustomerOrder o where " + dateArea.genConditionSQL("o.createDate", false) + orderState.genConditionSQL("o", true)))).getSingleResult();
            if (orderMoneySum == null) {
                orderMoneySum = new OrderMoneySeries();
            }
        }

        return orderMoneySum;
    }

    private OrderMoneySeries orderPayMoneySum = null;

    public OrderMoneySeries getOrderPayMoneySum() {
        if (orderPayMoneySum == null) {


            orderPayMoneySum = (OrderMoneySeries) dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.customerOrder.createDate", true))).setParameter("operType", AccountOper.AccountOperType.ORDER_SAVINGS).getSingleResult();
            if (orderPayMoneySum == null) {
                orderPayMoneySum = new OrderMoneySeries();
            }
        }
        return orderPayMoneySum;
    }

    private OrderMoneySeries prepareMoney = null;

    public OrderMoneySeries getPrepareMoney() {
        if (prepareMoney == null) {
            prepareMoney = (OrderMoneySeries) dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(accountOper.operMoney),count(accountOper.id)) from AccountOper accountOper where  accountOper.operType = :operType " + dateArea.genConditionSQL("accountOper.operDate", true))).setParameter("operType", AccountOper.AccountOperType.PRE_DEPOSIT).getSingleResult();
            if (prepareMoney == null) {
                prepareMoney = new OrderMoneySeries();
            }
        }
        return prepareMoney;
    }

}
