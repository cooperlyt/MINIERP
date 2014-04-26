package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/04/14
 * Time: 15:07
 */
@Name("saleMoneyChartData")
public class SaleMoneyChartData extends CustomerAreaChart {

    @In(create = true)
    private SearchDateArea searchDateArea;


    public void totalData() {
        genOrderMoneyData();

        genOutedOrderMoneyData();

        genBackMoneyData();

        genOrderReceiveMoneyData();

        genOrderNoReceiveMoneyData();

        genOrderArrearsMoneyData();
    }




    //订单总额

    private Map<String, OrderMoneySeries> orderMoneyData;

    private void genOrderMoneyData() {
        orderMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                        "sum(customerOrder.money), count(customerOrder.id)) from CustomerOrder customerOrder " +
                        "where customerOrder.canceled = false " +
                        searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                        " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            orderMoneyData.put(series.getKey(), series);
        }

        fillAreaData(orderMoneyData);
    }


    //已出库订单额

    private Map<String, OrderMoneySeries> outedOrderMoneyData;

    private void genOutedOrderMoneyData() {
        outedOrderMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                        "sum(customerOrder.money), count(customerOrder.id)) from CustomerOrder customerOrder " +
                        "where customerOrder.canceled = false and customerOrder.allStoreOut = true " +
                        searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                        " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            outedOrderMoneyData.put(series.getKey(), series);
        }

        fillAreaData(outedOrderMoneyData);
    }


    //退货总额 已完成

    private Map<String, OrderMoneySeries> backMoneyData;

    private void genBackMoneyData() {
        backMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(orderBack.customer.customerArea.id," +
                        "sum(orderBack.money), count(orderBack.id)) from  OrderBack orderBack " +
                        " where orderBack.moneyComplete = true and orderBack.resComplete = true " +
                        searchDateArea.genConditionSQL("orderBack.createDate", true) +
                        " group by orderBack.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            backMoneyData.put(series.getKey(), series);
        }

        fillAreaData(backMoneyData);
    }




    //已收款额

    private Map<String, OrderMoneySeries> orderReceiveMoneyData;

    private void genOrderReceiveMoneyData() {
        orderReceiveMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                        "sum(customerOrder.receiveMoney), count(customerOrder.id)) from CustomerOrder customerOrder " +
                        "where customerOrder.canceled = false " +
                        searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                        " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            orderReceiveMoneyData.put(series.getKey(), series);
        }

        fillAreaData(orderReceiveMoneyData);
    }

    //未收款额
    private Map<String, OrderMoneySeries> orderNoReceiveMoneyData;

    private void genOrderNoReceiveMoneyData() {
        orderNoReceiveMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                        "sum(COALESCE(customerOrder.money,0) - COALESCE(customerOrder.receiveMoney,0)), " +
                        "count(customerOrder.id)) from CustomerOrder customerOrder " +
                        "where customerOrder.canceled = false " +
                        searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                        " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            orderNoReceiveMoneyData.put(series.getKey(), series);
        }

        fillAreaData(orderNoReceiveMoneyData);
    }


    //欠收款额

    private Map<String, OrderMoneySeries> orderArrearsMoneyData;

    private void genOrderArrearsMoneyData() {
        orderArrearsMoneyData = new HashMap<String, OrderMoneySeries>(getCustomerAreas().size());


        List<OrderMoneySeries> areaResult = searchDateArea.setQueryParam(erpEntityManager.
                createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                        "sum(COALESCE(customerOrder.money,0) - COALESCE(customerOrder.receiveMoney,0)), " +
                        "count(customerOrder.id)) from CustomerOrder customerOrder " +
                        "where customerOrder.canceled = false and customerOrder.allStoreOut = true " +
                        searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                        " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();

        for (OrderMoneySeries series : areaResult) {
            orderArrearsMoneyData.put(series.getKey(), series);
        }

        fillAreaData(orderArrearsMoneyData);
    }


    //-----



    public Map<String, OrderMoneySeries> getOrderMoneyData() {
        return orderMoneyData;
    }

    public OrderMoneySeries getOrderMoneyTotal(){
        return totalDatas(orderMoneyData.values());
    }

    public Map<String, OrderMoneySeries> getOutedOrderMoneyData() {
        return outedOrderMoneyData;
    }

    public OrderMoneySeries getOutedOrderMoneyTotal(){
        return totalDatas(outedOrderMoneyData.values());
    }

    public Map<String, OrderMoneySeries> getBackMoneyData() {
        return backMoneyData;
    }

    public OrderMoneySeries getBackMoneyDataTotal(){
        return totalDatas(backMoneyData.values());
    }

    public Map<String, OrderMoneySeries> getOrderReceiveMoneyData() {
        return orderReceiveMoneyData;
    }

    public OrderMoneySeries getOrderReceiveMoneyTotal(){
        return totalDatas(orderReceiveMoneyData.values());
    }

    public Map<String, OrderMoneySeries> getOrderNoReceiveMoneyData() {
        return orderNoReceiveMoneyData;
    }

    public OrderMoneySeries getOrderNoReceiveMoneyTotal(){
        return totalDatas(orderNoReceiveMoneyData.values());
    }

    public Map<String, OrderMoneySeries> getOrderArrearsMoneyData() {
        return orderArrearsMoneyData;
    }

    public OrderMoneySeries getOrderArrearsMoneyTotal(){
        return totalDatas(orderArrearsMoneyData.values());
    }
}
