package com.dgsoft.erp.action;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.PayType;
import com.dgsoft.erp.model.api.ResCount;
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
    private DictionaryWord dictionary;

    @In
    private ResHelper resHelper;

    @Factory(value = "feePayTypes", scope = ScopeType.CONVERSATION)
    public PayType[] getFeePayTypes() {
        return new PayType[]{PayType.BANK_TRANSFER, PayType.CASH, PayType.CHECK};
    }

    @Factory(value = "middleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public CustomerOrder.MiddleMoneyCalcType[] getMiddleMoneyCalcTypes() {
        return CustomerOrder.MiddleMoneyCalcType.values();
    }

    @Factory(value = "itemMiddleMoneyCalcTypes", scope = ScopeType.CONVERSATION)
    public OrderItem.MiddleMoneyCalcType[] getItemMiddleMoneyCalcTypes() {
        return OrderItem.MiddleMoneyCalcType.values();
    }

    @Factory(value = "deliveryTypes", scope = ScopeType.CONVERSATION)
    public Dispatch.DeliveryType[] getDeliveryTypes() {
        return Dispatch.DeliveryType.values();
    }

    public String getToastMessages() {


        StringBuffer result = new StringBuffer();
        result.append(messages.get("OrderCode") + ":" + getInstance().getId() + "\n");

        if (getMasterNeedRes().isDispatched()) {
            for (Dispatch dispatch : getMasterNeedRes().getDispatches()) {
                result.append(dispatch.getStore().getName() + "\n");
                for (DispatchItem item : dispatch.getDispatchItemList()) {
                    result.append("\t" + resHelper.generateStoreResTitle(item.getStoreRes()) + " ");
                    result.append(item.getResCount().getMasterDisplayCount());
                    result.append("(" + item.getResCount().getDisplayAuxCount() + ")");
                }
            }

        } else {
            for (OrderItem item : getMasterNeedRes().getOrderItems()) {
                if (item.isStoreResItem()) {
                    result.append("\t" + resHelper.generateStoreResTitle(item.getStoreRes()) + ": ");
                    result.append(item.getStoreResCount().getMasterDisplayCount());
                    result.append("(" + item.getStoreResCount().getDisplayAuxCount() + ")\n");
                } else {
                    result.append("\t" + item.getRes().getName() + ": ");
                    result.append(BigDecimalFormat.format(item.getCount(), item.getMoneyUnit().getCountFormate()));
                    result.append(item.getMoneyUnit().getName() + "\n");
                }
            }
        }


        return result.toString();
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

    public NeedRes getLastNeedRes() {
        NeedRes result = null;
        for (NeedRes nr : getInstance().getNeedReses()) {
            if ((result == null) ||
                    (result.getCreateDate().getTime() < nr.getCreateDate().getTime()))
                result = nr;
        }
        return result;
    }

    public BigDecimal getTotalFare() {
        BigDecimal result = BigDecimal.ZERO;
        for (NeedRes nr : getInstance().getNeedReses()){
            result = result.add(nr.getTotalFare());
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


    public boolean isZeroPriceOrder(){
        return !(getInstance().getMoney().compareTo(BigDecimal.ZERO) > 0);
    }

    @DataModel
    public List<Map.Entry<StoreRes, ResCount>> getAllShipStoreResEntrySet() {
        List<Map.Entry<StoreRes, ResCount>> result = new ArrayList<Map.Entry<StoreRes, ResCount>>(allShipStoreReses().entrySet());

        Collections.sort(result, new Comparator<Map.Entry<StoreRes, ResCount>>() {
            @Override
            public int compare(Map.Entry<StoreRes, ResCount> o1, Map.Entry<StoreRes, ResCount> o2) {
                return o1.getKey().compareTo(o2.getKey());
            }
        });
        return result;
    }

    public BigDecimal getTotalItemMoney() {
        BigDecimal result = BigDecimal.ZERO;
        NeedRes needRes = getMasterNeedRes();
        if (needRes != null)
            for (OrderItem orderItem : needRes.getOrderItems()) {
                result = result.add(orderItem.getTotalMoney());
            }
        return result;

    }

    public BigDecimal getTotalItemMiddleMoney() {
        BigDecimal result = BigDecimal.ZERO;
        NeedRes needRes = getMasterNeedRes();
        if (needRes != null)
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


    public Map<StoreRes, ResCount> allShipStoreReses() {
        Map<StoreRes, ResCount> result = new HashMap<StoreRes, ResCount>();


        for (NeedRes nr : getInstance().getNeedReses()) {
            for (Dispatch dispatch : nr.getDispatches()) {
                if (dispatch.getStockChange() != null)
                    for (StockChangeItem sci : dispatch.getStockChange().getStockChangeItems()) {
                        ResCount count = result.get(sci.getStoreRes());
                        if (count == null) {
                            result.put(sci.getStoreRes(), sci.getResCount());
                        } else {
                            count.add(sci.getResCount());
                        }
                    }

//                for (DispatchItem di : dispatch.getDispatchItems()) {
//                    ResCount count = result.get(di.getStoreRes());
//                    if (count == null) {
//                        result.put(di.getStoreRes(), di.getResCount());
//                    } else {
//                        count.add(di.getResCount());
//                    }
//                }
            }
        }
        return result;
    }

    public boolean isAnyOneStoreOut() {
        for (NeedRes needRes : getInstance().getNeedReses()) {
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_STORE_OUT)) {
                    return true;
                }
            }
        }
        return false;
    }


    public boolean isAnyOneMoneyPay() {
        return !getInstance().getAccountOpers().isEmpty();
    }

    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (OrderItem item : getMasterNeedRes().getOrderItems()) {
            result = result.add(item.getTotalMoney());
        }
        return result;
    }

}
