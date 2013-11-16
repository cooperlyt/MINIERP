package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Factory;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/5/13
 * Time: 10:11 AM
 */
@Name("orderHome")
public class OrderHome extends ErpEntityHome<CustomerOrder> {

    @Factory(value = "deliveryTypes", scope = ScopeType.CONVERSATION)
    public Dispatch.DeliveryType[] getDeliveryTypes() {
        return Dispatch.DeliveryType.values();
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
    public Set<Map.Entry<StoreRes, ResCount>> getAllShipStoreResEntrySet() {
        return allShipStoreReses().entrySet();
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
