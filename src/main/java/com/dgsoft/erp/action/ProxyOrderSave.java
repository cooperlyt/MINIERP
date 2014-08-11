package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.AccountOper;
import com.dgsoft.erp.model.CustomerOrder;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by cooper on 8/7/14.
 */
@Name("proxyOrderSave")
@Scope(ScopeType.CONVERSATION)
public class ProxyOrderSave extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea where customerOrder.payType = 'EXPRESS_PROXY' and customerOrder.canceled = false";

    private static final String[] RESTRICTIONS = {

            "customerOrder.customer.customerArea.id = #{proxyOrderSave.customerAreaId}",
            "lower(customerOrder.customer.name) like lower(concat(#{proxyOrderSave.customerName},'%'))",
            "customerOrder.createDate >= #{searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{searchDateArea.searchDateTo}",
            "lower(customerOrder.id)  like lower(concat('%',#{proxyOrderSave.orderId}))",
            "customerOrder.payTag = #{proxyOrderSave.payed}",
            "customerOrder.customer.proxyAccountMoney > #{proxyOrderSave.minArrearsMoney}"};


    public ProxyOrderSave() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        setOrderColumn("customerOrder.createDate");
        setOrderDirection("desc");

    }

    private boolean onlyNoPayed = true;

    private boolean onlyArrears = true;

    private String customerAreaId;

    private String orderId;


    private String customerName;

    public Boolean getPayed() {
        if (onlyNoPayed) {
            return false;
        } else {
            return null;
        }
    }

    public BigDecimal getMinArrearsMoney() {
        if (onlyArrears) {
            return BigDecimal.ZERO;
        } else {
            return null;
        }
    }

    public boolean isOnlyNoPayed() {
        return onlyNoPayed;
    }

    public void setOnlyNoPayed(boolean onlyNoPayed) {
        this.onlyNoPayed = onlyNoPayed;
    }

    public String getCustomerAreaId() {
        return customerAreaId;
    }

    public void setCustomerAreaId(String customerAreaId) {
        this.customerAreaId = customerAreaId;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public boolean isOnlyArrears() {
        return onlyArrears;
    }

    public void setOnlyArrears(boolean onlyArrears) {
        this.onlyArrears = onlyArrears;
    }


    private List<BatchOperData<CustomerOrder>> customerOrderList;

    public List<BatchOperData<CustomerOrder>> getCustomerOrderList() {
        if (customerOrderList == null) {
            customerOrderList = new ArrayList<BatchOperData<CustomerOrder>>(getResultList().size());
            for (CustomerOrder order : getResultList()) {
                customerOrderList.add(new BatchOperData<CustomerOrder>(order, false));
            }
        }
        return customerOrderList;
    }


    @Override
    public void refresh() {
        super.refresh();
        customerOrderList = null;
    }

    @In(create = true)
    private MoneySaveHome moneySaveHome;

    @In
    private Credentials credentials;

    @In
    private FacesMessages facesMessages;

    public BigDecimal getSelectOrderMoneys() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getCustomerOrderList()) {
            if (order.isSelected()) {
                result = result.add(order.getData().getMoney());
            }
        }
        return result;
    }


    public String saveMoney() {

        for (BatchOperData<CustomerOrder> order : getCustomerOrderList()) {
            if (order.isSelected()) {
                boolean find = false;
                for (AccountOper oper : moneySaveHome.getAccountOperList()) {
                    if (oper.getCustomer().getId().equals(order.getData().getCustomer().getId())) {

                        find = true;
                        break;
                    }
                }
                if (!find) {
                    moneySaveHome.getAccountOperList().add(new AccountOper(moneySaveHome.getInstance(), AccountOper.AccountOperType.PROXY_SAVINGS, order.getData().getCustomer(), credentials.getUsername(), order.getData().getMoney()));
                }
                order.getData().setPayTag(true);
            }
        }


        if (moneySaveHome.getTotalReceiveProxyMoney().add(moneySaveHome.getTotalReceiveAccountMoney()).compareTo(moneySaveHome.getOperMoney()) != 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "proxyMoneyNotBalance", moneySaveHome.getOperMoney(), moneySaveHome.getTotalReceiveProxyMoney());
            return null;
        }


        return "next";
    }
}
