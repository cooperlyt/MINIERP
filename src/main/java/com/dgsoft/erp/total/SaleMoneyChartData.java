package com.dgsoft.erp.total;

import com.dgsoft.erp.model.CustomerArea;
import org.jboss.seam.annotations.Name;

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
public class SaleMoneyChartData extends OrderStateSearch{


    public void totalData() {
        genSaleMoneyData();
        genOverlayMoneyData();
        genReceiveMoneyData();

        List<CustomerArea> areas = erpEntityManager.createQuery("select area from CustomerArea area ", CustomerArea.class).getResultList();
        if (areas.isEmpty()) {


        }
        for (CustomerArea area: areas){
            if (saleMoneyData.get(area.getId()) == null){
                saleMoneyData.put(area.getId(),new OrderMoneySeries(area.getName()));
            }else{
                saleMoneyData.get(area.getId()).setKey(area.getName());
            }

            if (overlayMoneyData.get(area.getId()) == null){
                overlayMoneyData.put(area.getId(),new OrderMoneySeries(area.getName()));
            }else{
                overlayMoneyData.get(area.getId()).setKey(area.getName());
            }
            if (receiveMoneyData.get(area.getId()) == null){
                receiveMoneyData.put(area.getId(),new OrderMoneySeries(area.getName()));
            }else{
                receiveMoneyData.get(area.getId()).setKey(area.getName());
            }
        }

    }

    private Map<String, OrderMoneySeries> overlayMoneyData;

    public void genOverlayMoneyData(){

        OrderMoneySeries result = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(customerOrder.money - customerOrder.receiveMoney),count(customerOrder.id)) from CustomerOrder customerOrder where customerOrder.receiveMoney <  customerOrder.money " + dateArea.genConditionSQL("customerOrder.createDate", true) + orderState.genConditionSQL("customerOrder", true),OrderMoneySeries.class))).getSingleResult();
        overlayMoneyData = new HashMap<String, OrderMoneySeries>();
        overlayMoneyData.put("all",result);
        List<OrderMoneySeries> areaResult = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id,sum(customerOrder.money - customerOrder.receiveMoney),count(customerOrder.id)) from CustomerOrder customerOrder where customerOrder.receiveMoney <  customerOrder.money " + dateArea.genConditionSQL("customerOrder.createDate", true) + orderState.genConditionSQL("customerOrder", true) + " group by customerOrder.customer.customerArea.id",OrderMoneySeries.class))).getResultList();

        for(OrderMoneySeries series : areaResult){
            overlayMoneyData.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> getOverlayMoneyData() {
        return overlayMoneyData;
    }

    private Map<String, OrderMoneySeries> receiveMoneyData;

    public void genReceiveMoneyData(){
        OrderMoneySeries result = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(customerOrder.receiveMoney),count(customerOrder.id)) from CustomerOrder customerOrder where " + dateArea.genConditionSQL("customerOrder.createDate", false) + orderState.genConditionSQL("customerOrder", true),OrderMoneySeries.class))).getSingleResult();
        receiveMoneyData = new HashMap<String, OrderMoneySeries>();
        receiveMoneyData.put("all",result);
        List<OrderMoneySeries> areaResult = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id,sum(customerOrder.receiveMoney),count(customerOrder.id)) from CustomerOrder customerOrder where " + dateArea.genConditionSQL("customerOrder.createDate", false) + orderState.genConditionSQL("customerOrder", true) + " group by customerOrder.customer.customerArea.id",OrderMoneySeries.class))).getResultList();

        for(OrderMoneySeries series : areaResult){
            receiveMoneyData.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> getReceiveMoneyData() {
        return receiveMoneyData;
    }

    private Map<String, OrderMoneySeries> saleMoneyData;

    public void genSaleMoneyData(){
        OrderMoneySeries result = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(sum(customerOrder.money),count(customerOrder.id)) from CustomerOrder customerOrder where " + dateArea.genConditionSQL("customerOrder.createDate", false) + orderState.genConditionSQL("customerOrder", true),OrderMoneySeries.class))).getSingleResult();
        saleMoneyData = new HashMap<String, OrderMoneySeries>();
        saleMoneyData.put("all",result);
        List<OrderMoneySeries> areaResult = orderState.setQueryParam(dateArea.setQueryParam(erpEntityManager.createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id,sum(customerOrder.money),count(customerOrder.id)) from CustomerOrder customerOrder where " + dateArea.genConditionSQL("customerOrder.createDate", false) + orderState.genConditionSQL("customerOrder", true) + " group by customerOrder.customer.customerArea.id",OrderMoneySeries.class))).getResultList();

        for(OrderMoneySeries series : areaResult){
            saleMoneyData.put(series.getKey(), series);
        }
    }

    public Map<String, OrderMoneySeries> getSaleMoneyData() {
        return saleMoneyData;
    }

}
