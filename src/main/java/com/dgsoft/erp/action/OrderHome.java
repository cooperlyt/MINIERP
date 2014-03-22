package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.PayType;
import com.dgsoft.erp.model.api.ResCount;
import com.dgsoft.erp.model.api.StoreResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.In;
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

    @In(create = true)
    private Map<String, String> messages;

    @In
    private ResHelper resHelper;

    @Factory(value = "feePayTypes", scope = ScopeType.CONVERSATION)
    public PayType[] getFeePayTypes() {
        return new PayType[]{PayType.BANK_TRANSFER, PayType.CASH, PayType.CHECK};
    }

    public enum ItemMiddleMoneyCalcType {
        NOT_CALC, ITEM_FIX, ITEM_RATE, CROSS_CALC;
    }

    @Factory(value = "allItemMiddleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public ItemMiddleMoneyCalcType[] getAllItemMiddleMoneyCalcTypes() {
        return ItemMiddleMoneyCalcType.values();
    }

    @Factory(value = "deliveryTypes", scope = ScopeType.CONVERSATION)
    public Dispatch.DeliveryType[] getDeliveryTypes() {
        return Dispatch.DeliveryType.values();
    }

    public String getToastMessages() {


        StringBuffer result = new StringBuffer();
        result.append(messages.get("OrderCode") + ":" + getInstance().getId() + "\n");

        result.append(messages.get("Customer") + ":" + getInstance().getCustomer().getName());
        result.append("\n");
        result.append(messages.get("order_field_reveiveContact") + ":" + getLastNeedRes().getReceivePerson());
        result.append(" ");
        result.append(messages.get("order_field_reveiveTel") + " " + getLastNeedRes().getReceiveTel());
        result.append("\n");
        result.append(messages.get("address") + ":" + getLastNeedRes().getAddress());
        result.append("\n");

        if (getMasterNeedRes().getStatus().equals(NeedRes.NeedResStatus.DISPATCHED)) {
            for (Dispatch dispatch : getMasterNeedRes().getDispatches()) {
                result.append(dispatch.getStore().getName() + "\n");
                for (DispatchItem item : dispatch.getDispatchItemList()) {
                    result.append("\t" + resHelper.generateStoreResTitle(item.getStoreRes()) + " ");
                    result.append(item.getDisplayMasterCount());
                    if (!DataFormat.isEmpty(item.getDisplayAuxCount()))
                        result.append("(" + item.getDisplayAuxCount() + ")");
                }
            }

        } else {
            for (OrderItem item : getMasterNeedRes().getOrderItems()) {

                result.append("\t" + resHelper.generateStoreResTitle(item.getStoreRes()) + ": ");
                result.append(item.getDisplayMasterCount());
                if (!DataFormat.isEmpty(item.getDisplayAuxCount()))
                    result.append("(" + item.getDisplayAuxCount() + ")\n");

            }
        }


        return result.toString();
    }


    public BigDecimal getAllFare() {
        BigDecimal result = BigDecimal.ZERO;

        for (NeedRes nr : getInstance().getNeedReses()) {
            result = result.add(nr.getTotalFare());
        }

        return result;
    }

    public BigDecimal getTotalFare() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes nr : getInstance().getNeedReses()) {
            result = result.add(nr.getTotalFare());
        }
        return result;
    }


    public boolean isHaveOverlyOut() {
        for (Dispatch dispatch : getLastNeedRes().getDispatches()) {
            if (!dispatch.getOverlyOuts().isEmpty()) {
                return true;
            }
        }
        return false;
    }

    public NeedRes getLastNeedRes() {
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


    public boolean isZeroPriceOrder() {
        return !(getInstance().getMoney().compareTo(BigDecimal.ZERO) > 0);
    }

    @DataModel
    public List<Map.Entry<StoreRes, StoreResCount>> getAllShipStoreResEntrySet() {
        List<Map.Entry<StoreRes, StoreResCount>> result = new ArrayList<Map.Entry<StoreRes, StoreResCount>>(allShipStoreReses().entrySet());

        Collections.sort(result, new Comparator<Map.Entry<StoreRes, StoreResCount>>() {
            @Override
            public int compare(Map.Entry<StoreRes, StoreResCount> o1, Map.Entry<StoreRes, StoreResCount> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public BigDecimal getLastResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        NeedRes needRes = getMasterNeedRes();
        if (needRes != null)
            for (OrderItem orderItem : needRes.getOrderItems()) {
                result = result.add(orderItem.getTotalPrice());
            }
        return result;
    }

    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (OrderItem orderItem : needRes.getOrderItems()) {
                result = result.add(orderItem.getTotalPrice());
            }
        }
        return result;
    }


    public List<AccountOper> getOrderPayOpers() {
        List<AccountOper> result = new ArrayList<AccountOper>();
        for (AccountOper oper : getInstance().getAccountOperList()) {
            if (oper.getOperType().equals(AccountOper.AccountOperType.ORDER_PAY) ||
                    oper.getOperType().equals(AccountOper.AccountOperType.ORDER_EARNEST)) {
                result.add(oper);
            }
        }
        return result;
    }


    public BigDecimal getTotalItemMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes needRes : getInstance().getNeedReses())
            for (OrderItem orderItem : needRes.getOrderItems()) {
                if ((orderItem.getMiddleMoneyCalcType() != null) &&
                        (orderItem.getMiddleMoney() != null))
                    result = result.add(orderItem.getMiddleMoney());
            }

        return result;
    }

    public BigDecimal getTotalOrderFeeMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderFee orderFee : getInstance().getOrderFees()) {
            result = result.add(orderFee.getMoney());
        }
        return result;
    }

    public BigDecimal getShortageMoney() {
        return getInstance().getMoney().subtract(getTotalReveiveMoney());
    }

    public BigDecimal getTotalReveiveMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper oper : getInstance().getAccountOpers()) {
            if (oper.getOperType().equals(AccountOper.AccountOperType.ORDER_EARNEST) ||
                    oper.getOperType().equals(AccountOper.AccountOperType.ORDER_PAY))
                result = result.add(oper.getOperMoney());
        }
        return result;
    }

    public BigDecimal getReveiveEarnest() {
        BigDecimal result = BigDecimal.ZERO;
        for (AccountOper oper : getInstance().getAccountOpers()) {
            if (oper.getOperType().equals(AccountOper.AccountOperType.ORDER_EARNEST))
                result = result.add(oper.getOperMoney());
        }
        return result;
    }


    public Map<StoreRes, StoreResCount> allShipStoreReses() {
        return getInstance().getAllShipStoreReses();
    }

    public boolean isAnyOneStoreOut() {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.isStoreOut()) {
                    return true;
                }
            }
        }
        return false;
    }


    public OrderItem getFirstResOrderItem(StoreRes storeRes) {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (OrderItem oi : needRes.getOrderItems()) {
                if (oi.getStoreRes().equals(storeRes) && (oi.getMoney().compareTo(BigDecimal.ZERO) > 0)) {
                    return oi;
                }
            }
        }
        return null;
    }

    public boolean isAnyOneMoneyPay() {
        return !getInstance().getAccountOpers().isEmpty();
    }

//    public  getFirstPrice(StoreRes storeRes){
//
//        for (NeedRes needRes: getInstance().getNeedReses()){
//            for (OrderItem orderItem: needRes.getOrderItems()){
//                if (orderItem.getStoreRes().equals(storeRes)){
//                    return orderItem.getPrice();
//                }
//            }
//        }
//        return null;
//    }

    public boolean isComplete() {
        return !getInstance().isCanceled() && getInstance().isAllStoreOut() && getInstance().isMoneyComplete() && getInstance().isResReceived();
    }

}
