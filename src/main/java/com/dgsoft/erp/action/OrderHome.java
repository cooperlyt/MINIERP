package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.PayType;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:11 AM
 */
@Name("orderHome")
public class OrderHome extends ErpEntityHome<CustomerOrder> {

    @Factory(value = "feePayTypes",scope = ScopeType.CONVERSATION)
    public PayType[] getFeePayTypes(){
        return new PayType[]{PayType.BANK_TRANSFER,PayType.CASH,PayType.CHECK};
    }

    @Factory(value = "middleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public CustomerOrder.MiddleMoneyCalcType[] getMiddleMoneyCalcTypes(){
        return CustomerOrder.MiddleMoneyCalcType.values();
    }

    @Factory (value ="itemMiddleMoneyCalcTypes" , scope = ScopeType.CONVERSATION)
    public OrderItem.MiddleMoneyCalcType[] getItemMiddleMoneyCalcTypes(){
        return OrderItem.MiddleMoneyCalcType.values();
    }

    @Factory(value = "deliveryTypes", scope = ScopeType.CONVERSATION)
    public Dispatch.DeliveryType[] getDeliveryTypes() {
        return Dispatch.DeliveryType.values();
    }

    @Factory(value = "orderStates", scope = ScopeType.CONVERSATION)
    public CustomerOrder.OrderState[] getOrderStates(){
        return CustomerOrder.OrderState.values();
    }

    public BigDecimal getAllProxyFare() {
        BigDecimal result = BigDecimal.ZERO;
        if (getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY))
            for (NeedRes nr : getInstance().getNeedReses()) {
                if (!nr.isFareByCustomer()) {
                    result = result.add(nr.getProxyFare());
                }
            }
        return result;
    }

    public BigDecimal getAllFare() {
        BigDecimal result = getAllProxyFare();

        for (NeedRes nr : getInstance().getNeedReses()) {
            if (!nr.isFareByCustomer()) {
                for (Dispatch dispatch : nr.getDispatches()) {
                    if (dispatch.getDeliveryType().isHaveFare() &&
                            dispatch.getFare() != null)
                        result = result.add(dispatch.getFare());
                }
            }
        }

        return result;
    }

    public NeedRes getLastNeedRes(){
        NeedRes result = null;
        for (NeedRes nr : getInstance().getNeedReses()) {
            if ((result == null) ||
                    (result.getCreateDate().getTime() < nr.getCreateDate().getTime()))
                result = nr;
        }
        return result;
    }

    public NeedRes getMasterNeedRes() {
        for (NeedRes nr : getInstance().getNeedReses()) {
            if (nr.getType().equals(NeedRes.NeedResType.ORDER_SEND)) {
                return nr;
            }
        }
        return null;
    }

    public boolean isHavePayFee() {
        for (OrderFee fee : getInstance().getOrderFees()) {
            if (!fee.isPay()) {
                return true;
            }
        }
        return false;
    }


    @DataModel
    public List<Map.Entry<StoreRes, ResCount>> getAllShipStoreResEntrySet() {
        List<Map.Entry<StoreRes, ResCount>> result = new ArrayList<Map.Entry<StoreRes, ResCount>>(allShipStoreReses().entrySet());

        Collections.sort(result,new Comparator<Map.Entry<StoreRes, ResCount>>() {
            @Override
            public int compare(Map.Entry<StoreRes, ResCount> o1, Map.Entry<StoreRes, ResCount> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public BigDecimal getTotalItemMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem orderItem: getMasterNeedRes().getOrderItems()){
            result = result.add(orderItem.getTotalMoney());
        }
        return result;

    }

    public BigDecimal getTotalItemMiddleMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem orderItem: getMasterNeedRes().getOrderItems()){
            if ((orderItem.getMiddleMoneyCalcType() != null) &&
                    (orderItem.getMiddleMoney() != null))
            result = result.add(orderItem.getMiddleMoney());
        }
        return result;
    }

    public BigDecimal getTotalCustomerPayMoney(){
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper accountOper: getInstance().getAccountOpers()){
            result = result.add(accountOper.getOperMoney());
        }
        return result;
    }

    public Map<StoreRes, ResCount> allShipStoreReses() {
        Map<StoreRes, ResCount> result = new HashMap<StoreRes, ResCount>();


        for (NeedRes nr : getInstance().getNeedReses()) {
            for (Dispatch dispatch : nr.getDispatches()) {
                for (DispatchItem di : dispatch.getDispatchItems()) {
                    ResCount count = result.get(di.getStoreRes());
                    if (count == null) {
                        result.put(di.getStoreRes(), di.getResCount());
                    } else {
                        count.add(di.getResCount());
                    }
                }
            }
        }
        return result;
    }


}
