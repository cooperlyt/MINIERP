package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cooper on 11/23/14.
 */
@Name("orderSelectList")
@Scope(ScopeType.CONVERSATION)
public class OrderSelectList extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea where customerOrder.canceled <> true and customerOrder.money > 0";

    private static final String[] RESTRICTIONS = {
            "customerOrder.payType = #{orderSelectList.payTypeCondition}",
            "customerOrder.payType <> #{orderSelectList.notPayTypeCondition}",
            "customerOrder.customer.customerArea.id = #{orderSelectList.customerAreaId}",
            "lower(customerOrder.customer.name) like lower(concat('%',#{orderSelectList.customerName},'%'))",
            "customerOrder.createDate >= #{orderSelectList.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{orderSelectList.searchDateArea.searchDateTo}",
            "customerOrder.payTag = #{orderSelectList.flagCondition}",
            "lower(customerOrder.id)  like lower(concat('%',#{orderSelectList.orderId}))",
            "customerOrder.allStoreOut = #{orderSelectList.allStoreOut}"};

    private SearchDateArea searchDateArea = new SearchDateArea(null,null);

    private String customerAreaId;

    private String customerName;

    private String orderId;

    private Boolean allStoreOut;

    private boolean onlyNotFlag = true;

    private String selectOrderId;

    private boolean proxyPay = false;

    public CustomerOrder.OrderPayType getPayTypeCondition() {
        return proxyPay ? CustomerOrder.OrderPayType.EXPRESS_PROXY : null;
    }

    public CustomerOrder.OrderPayType getNotPayTypeCondition(){
        return proxyPay ?  null :CustomerOrder.OrderPayType.EXPRESS_PROXY;
    }

    private List<CustomerOrder> selectOrders = new ArrayList<CustomerOrder>();

    public OrderSelectList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setMaxResults(15);
        setOrderColumn("customerOrder.createDate");
        setOrderDirection("desc");
    }

    public List<BatchOperData<CustomerOrder>> getResultDatas() {
        List<BatchOperData<CustomerOrder>> result = new ArrayList<BatchOperData<CustomerOrder>>(getResultList().size());
        for (CustomerOrder order : getResultList()) {
            result.add(new BatchOperData<CustomerOrder>(order, selectOrders.contains(order)));
        }
        return result;
    }

    public boolean isSelectAll() {
        if (getResultList().isEmpty()) {
            return false;
        }
        for (CustomerOrder order : getResultList()) {
            if (!selectOrders.contains(order)) {
                return false;
            }
        }
        return true;
    }

    public void setSelectAll(boolean value) {
        if (value) {
            for (CustomerOrder order : getResultList()) {
                if (!selectOrders.contains(order)) {
                    selectOrders.add(order);
                }
            }
        } else {
            for (CustomerOrder order : getResultList()) {
                selectOrders.remove(order);
            }
        }
    }


    public void orderSelection() {
        boolean selected = false;
        for (CustomerOrder order : selectOrders) {
            if (order.getId().equals(selectOrderId)) {
                selectOrders.remove(order);
                selected = true;
                break;
            }
        }
        if (!selected) {
            for (CustomerOrder order : getResultList()) {
                if (order.getId().equals(selectOrderId)) {
                    selectOrders.add(order);
                    break;
                }
            }
        }
    }

    public void removeSelected(){
        for (CustomerOrder order: selectOrders){
            if (order.getId().equals(selectOrderId)){
                selectOrders.remove(order);
                return;
            }
        }

    }

    public BigDecimal getSelectOrderTotalMoney(){
         BigDecimal result = BigDecimal.ZERO;
        for (CustomerOrder order: selectOrders){
            result = result.add(order.getMoney());
        }
        return result;
    }

    public Boolean getFlagCondition() {
        return onlyNotFlag ? false : null;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public List<CustomerOrder> getSelectOrders() {
        return selectOrders;
    }

    public Boolean getAllStoreOut() {
        return allStoreOut;
    }

    public void setAllStoreOut(Boolean allStoreOut) {
        this.allStoreOut = allStoreOut;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isOnlyNotFlag() {
        return onlyNotFlag;
    }

    public void setOnlyNotFlag(boolean onlyNotFlag) {
        this.onlyNotFlag = onlyNotFlag;
    }

    public String getSelectOrderId() {
        return selectOrderId;
    }

    public void setSelectOrderId(String selectOrderId) {
        this.selectOrderId = selectOrderId;
    }

    public boolean isProxyPay() {
        return proxyPay;
    }

    public void setProxyPay(boolean proxyPay) {
        this.proxyPay = proxyPay;
    }
}
