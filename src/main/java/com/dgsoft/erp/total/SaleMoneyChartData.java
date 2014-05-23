package com.dgsoft.erp.total;

import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.model.CustomerArea;
import com.dgsoft.erp.total.data.AreaResSaleGroupData;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.ArrayList;
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

    private boolean executed = false;

    public boolean isExecuted() {
        return executed;
    }

    public void totalData(){
        executed = true;
        refresh();
    }

    public void refresh(){
        areaResMoneyData = null;
        orderMoneyData = null;
        backMoneyData = null;
    }

    //provinceMoneyData;


    private Map<String, List<AreaResSaleGroupData>> areaResMoneyData;

    private void initAreaResMoneyData() {

        if (areaResMoneyData == null) {
            List<AreaResSaleGroupData> subList = searchDateArea.setQueryParam(erpEntityManager.createQuery(
                    "select new com.dgsoft.erp.total.data.AreaResSaleGroupData(orderItem.needRes.customerOrder.customer.customerArea.id," +
                            " max(orderItem.needRes.customerOrder.customer.customerArea.name)," +
                            "orderItem.storeRes.res.id,max(orderItem.storeRes.res.name),sum(orderItem.totalMoney),sum(orderItem.count) ) from OrderItem orderItem " +
                            "where orderItem.needRes.customerOrder.canceled = false and orderItem.needRes.customerOrder.allStoreOut = true and" +
                            " orderItem.status = 'COMPLETED' and orderItem.storeRes.res.resCategory.type = 'PRODUCT' " + searchDateArea.genConditionSQL("orderItem.needRes.customerOrder.createDate", true) +
                            " group by orderItem.needRes.customerOrder.customer.customerArea.id,orderItem.storeRes.res.id", AreaResSaleGroupData.class
            )).getResultList();

            Logging.getLog(this.getClass()).debug(searchDateArea.getDateFrom() + "-" + searchDateArea.getSearchDateTo() + "|sql return count:" + subList.size());

            areaResMoneyData = new HashMap<String, List<AreaResSaleGroupData>>();

            for (AreaResSaleGroupData arsgd : subList) {
                List<AreaResSaleGroupData> value = areaResMoneyData.get(arsgd.getResName());
                if (value == null) {
                    value = new ArrayList<AreaResSaleGroupData>();
                    areaResMoneyData.put(arsgd.getResName(), value);
                }
                value.add(arsgd);
            }
        }
    }

    public Map<String, List<AreaResSaleGroupData>> getAreaResMoneyData() {
        initAreaResMoneyData();
        return areaResMoneyData;
    }

    public List<Map.Entry<String,List<AreaResSaleGroupData>>> getAreaResMoneyDataList(){
        return new ArrayList<Map.Entry<String, List<AreaResSaleGroupData>>>(getAreaResMoneyData().entrySet());
    }


    //订单总额

    private List<OrderMoneySeries> orderMoneyData;

    private void initOrderMoneyData() {
        if (orderMoneyData == null) {


            orderMoneyData = searchDateArea.setQueryParam(erpEntityManager.
                    createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(customerOrder.customer.customerArea.id," +
                            "sum(customerOrder.money), count(customerOrder.id)) from CustomerOrder customerOrder " +
                            "where customerOrder.canceled = false and customerOrder.allStoreOut = true " +
                            searchDateArea.genConditionSQL("customerOrder.createDate", true) +
                            " group by customerOrder.customer.customerArea.id", OrderMoneySeries.class)).getResultList();



           // fillAreaData(orderMoneyData);
        }
    }

    public List<OrderMoneySeries> getOrderMoneyData(){
        initOrderMoneyData();
        return orderMoneyData;
    }

    public Map<String, OrderMoneySeries> getOrderMoneyDataMap() {

        Map<String, OrderMoneySeries> result = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : getOrderMoneyData()) {
            result.put(series.getKey(), series);
        }
        return result;
    }


    public OrderMoneySeries getOrderMoneyTotal() {
        return totalDatas(getOrderMoneyData());
    }


    //退货总额 已完成

    private List<OrderMoneySeries> backMoneyData;

    private void initBackMoneyData() {
        if (backMoneyData == null) {

            backMoneyData = searchDateArea.setQueryParam(erpEntityManager.
                    createQuery("select new com.dgsoft.erp.total.OrderMoneySeries(orderBack.customer.customerArea.id," +
                            "sum(orderBack.money), count(orderBack.id)) from  OrderBack orderBack " +
                            " where orderBack.moneyComplete = true and orderBack.resComplete = true " +
                            searchDateArea.genConditionSQL("orderBack.createDate", true) +
                            " group by orderBack.customer.customerArea.id", OrderMoneySeries.class)).getResultList();



        }
        //fillAreaData(backMoneyData);
    }


    //-----


    public List<OrderMoneySeries> getBackMoneyData() {
        initBackMoneyData();
        return backMoneyData;
    }

    public Map<String, OrderMoneySeries> getBackMoneyDataMap() {
        Map<String, OrderMoneySeries> result = new HashMap<String, OrderMoneySeries>();
        for (OrderMoneySeries series : getBackMoneyData()) {
            result.put(series.getKey(), series);
        }
        return result;
    }

    public OrderMoneySeries getBackMoneyDataTotal() {
        return totalDatas(getBackMoneyData());
    }


}
