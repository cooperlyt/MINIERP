package com.dgsoft.erp.action;

import com.dgsoft.common.BatchOperData;
import com.dgsoft.common.SearchDateArea;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.BatchOperEntity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

import javax.persistence.Transient;
import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 1/12/14.
 */
@Name("middleManRewardCalc")
@Scope(ScopeType.CONVERSATION)
public class MiddleManRewardCalc extends ErpEntityQuery<CustomerOrder> {

    private static final String EJBQL = "select customerOrder from CustomerOrder customerOrder left join fetch customerOrder.customer customer left join fetch customer.customerArea customerArea";

    private static final String[] RESTRICTIONS = {
            "customerOrder.customer.middleMan.id = #{middleManHome.instance.id}",
            "customerOrder.createDate >= #{middleManRewardCalc.searchDateArea.dateFrom}",
            "customerOrder.createDate <= #{middleManRewardCalc.searchDateArea.searchDateTo}",
            "customerOrder.canceled = #{middleManRewardCalc.canceled}",
            "customerOrder.allStoreOut = #{middleManRewardCalc.allStoreOut}",
            "customerOrder.resReceived = #{middleManRewardCalc.customerConfirm}",
            "customerOrder.moneyComplete = #{middleManRewardCalc.moneyComplete}",
            "customerOrder.middlePayed = #{middleManRewardCalc.containPayed}"};

    private boolean allCustomerSelected = false;

    private boolean allOrderSelected = false;


    //private CustomerOrder.MiddleMoneyCalcType orderCalcType;

    //public BigDecimal allOrderRate;

    //public BigDecimal allOrderMiddleMoney;

    public List<BatchOperData<Customer>> containCustomers;

    public List<OrderCalcItem> containOrders;

    public MiddleManRewardCalc() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
        //setMaxResults(100);
    }

    private SearchDateArea searchDateArea = new SearchDateArea(new Date(),new Date());

    private Boolean containPayed = false;

    private Boolean canceled = false;

    private Boolean allStoreOut;

    private Boolean moneyComplete = true;

    private Boolean customerConfirm;


    public Boolean getContainPayed() {
        return containPayed;
    }

    public void setContainPayed(Boolean containPayed) {
        this.containPayed = containPayed;
    }

    public SearchDateArea getSearchDateArea() {
        return searchDateArea;
    }

    public Boolean getAllStoreOut() {
        return allStoreOut;
    }

    public void setAllStoreOut(Boolean allStoreOut) {
        this.allStoreOut = allStoreOut;
    }

    public Boolean getCanceled() {
        return canceled;
    }

    public void setCanceled(Boolean canceled) {
        this.canceled = canceled;
    }

    public Boolean getMoneyComplete() {
        return moneyComplete;
    }

    public void setMoneyComplete(Boolean moneyComplete) {
        this.moneyComplete = moneyComplete;
    }

    public Boolean getCustomerConfirm() {
        return customerConfirm;
    }

    public void setCustomerConfirm(Boolean customerConfirm) {
        this.customerConfirm = customerConfirm;
    }


    //------------------------------------

    public boolean isAllOrderSelected() {
        return allOrderSelected;
    }

    public void setAllOrderSelected(boolean allOrderSelected) {
        this.allOrderSelected = allOrderSelected;
    }

    public boolean isAllCustomerSelected() {
        return allCustomerSelected;
    }

    public void setAllCustomerSelected(boolean allCustomerSelected) {
        this.allCustomerSelected = allCustomerSelected;
    }


    private RebateProgram selectProgram;

    public RebateProgram getSelectProgram() {
        return selectProgram;
    }

    public void setSelectProgram(RebateProgram selectProgram) {
        this.selectProgram = selectProgram;
    }

//    public void calcAllOrderMiddleMoney() {
//        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
//            if (order.isSelected()) {
//                order.getData().setMiddleMoneyCalcType(orderCalcType);
//                order.getData().setMiddleRate(allOrderRate);
//                order.getData().setMiddleMoney(allOrderMiddleMoney);
//                order.getData().calcOrderMiddleMoney();
//            }
//        }
//    }


    public void allCustomerCheckChange() {
        for (BatchOperData<Customer> customer : getContainCustomers()) {
            customer.setSelected(allCustomerSelected);
        }
        refreshOrder();
    }

    public void customerCheckChange() {

        allCustomerSelected = true;
        for (BatchOperData<Customer> customer : getContainCustomers()) {
            if (!customer.isSelected()) {
                allCustomerSelected = false;
                break;
            }
        }

        refreshOrder();

    }



    public void allOrderCheckChange() {
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            order.setSelected(allOrderSelected);
            if (allOrderSelected) {
                order.getData().setMiddleMoney(BigDecimal.ZERO);
                order.getData().setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                order.getData().setMiddleTotal(null);
                order.getData().setMiddleRate(null);
            } else {
                order.getData().setMiddleMoney(null);
                order.getData().setMiddleMoneyCalcType(null);
                order.getData().setMiddleTotal(null);
                order.getData().setMiddleRate(null);
            }
        }
    }

    public void orderCheckChange() {
        allOrderSelected = true;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (!order.isSelected()) {
                allOrderSelected = false;
                break;
            }
        }
//        Set<Res> reses = genContainOrderReses();
//        Set<ResMiddleMoney> removeRmm = new HashSet<ResMiddleMoney>();
//        for (ResMiddleMoney rmm : getContainReses()) {
//            if (!reses.remove(rmm.getRes())) {
//                removeRmm.add(rmm);
//            }
//        }
//        getContainReses().removeAll(removeRmm);
//        for (Res res : reses) {
//            getContainReses().add(new ResMiddleMoney(res));
//        }
    }


    private void refreshOrder() {
        containOrders = null;
        allOrderSelected = false;
    }


    public List<BatchOperData<Customer>> getContainCustomers() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainCustomers();
        return containCustomers;
    }

    private void initContainCustomers() {
        if (containCustomers == null) {
            Set<Customer> customers = new HashSet<Customer>();
            for (CustomerOrder order : getResultList()) {
               // order.getCustomer().setSelected(false);
                customers.add(order.getCustomer());
            }
            containCustomers = new ArrayList<BatchOperData<Customer>>(BatchOperData.createBatchOperDataList(customers,false));
        }
    }

    public List<OrderCalcItem> getContainOrders() {
        if (isAnyParameterDirty()) {
            refresh();
        }
        initContainOrders();

        return containOrders;
    }

    private void initContainOrders() {
        if (containOrders == null) {
            initContainCustomers();
            containOrders = new ArrayList<OrderCalcItem>();
            for (CustomerOrder order : getResultList()) {
                for (BatchOperData<Customer> customer : containCustomers) {
                    if (customer.isSelected() && customer.getData().getId().equals(order.getCustomer().getId())) {
                        //order.setSelected(false);
                        order.setMiddleMoney(BigDecimal.ZERO);
                        order.setMiddleMoneyCalcType(RebateProgram.OrderRebateMode.NOT_CALC);
                        order.setMiddleTotal(null);
                        order.setMiddleRate(null);
                        containOrders.add(new OrderCalcItem(order,false));
                        break;
                    }
                }
            }
        }
    }

    public int getSelectOrderCount() {
        int result = 0;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected())
                result++;
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected())
                result = result.add(order.getData().getMoney());
        }
        return result;
    }

    public BigDecimal getContainOrderTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            result = result.add(order.getData().getMoney());
        }
        return result;
    }

    public BigDecimal getSelectOrderTotalMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BatchOperData<CustomerOrder> order : getContainOrders()) {
            if (order.isSelected() && (order.getData().getMiddleMoney() != null))
                result = result.add(order.getData().getMiddleMoney());
        }
        return result;
    }

    @Override
    public void refresh() {
        super.refresh();
        refreshOrder();
        containCustomers = null;
        allCustomerSelected = false;
    }

    public static class OrderCalcItem extends BatchOperData<CustomerOrder>{

        private RebateProgram rebateProgram;

        private boolean calcItem = false;

        public OrderCalcItem(CustomerOrder data, boolean selected) {
            super(data, selected);
            if (getData().getCustomer().getMiddleMan() != null){
                rebateProgram = getData().getCustomer().getMiddleMan().getRebateProgram();
            }
            init();
        }

        private void init(){

            if (rebateProgram != null){
                getData().setMiddleMoneyCalcType(rebateProgram.getOrderMode());
                getData().setMiddleRate(rebateProgram.getRebate());
                calcItem = rebateProgram.isCalcItem();
                if (calcItem){
                    for (NeedRes needRes: getData().getNeedReses()){
                        for(OrderItem orderItem: needRes.getOrderItems()){
                            orderItem.setMiddleRate();
                        }
                    }
                }


                calcOrderRebate();
            }
        }

        public RebateProgram getRebateProgram() {
            return rebateProgram;
        }

        public void setRebateProgram(RebateProgram rebateProgram) {
            this.rebateProgram = rebateProgram;
        }

        public boolean isCalcItem() {
            return calcItem;
        }

        public void setCalcItem(boolean calcItem) {
            this.calcItem = calcItem;
        }

        public void calcOrderRebate(){

        }
    }

}
