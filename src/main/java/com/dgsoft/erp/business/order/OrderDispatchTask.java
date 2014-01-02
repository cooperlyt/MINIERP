package com.dgsoft.erp.business.order;

import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.action.CarsHome;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.TransCorpHome;
import com.dgsoft.erp.action.store.StoreResCountInupt;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.ResCount;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 11/9/13
 * Time: 4:15 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("orderDispatchTask")
public class OrderDispatchTask extends OrderTaskHandle {

    @In
    private DictionaryWord dictionary;

    @In(create = true)
    private Map<String, String> messages;


    @In(create = true)
    private NeedResHome needResHome;

    @In(create = true)
    private OrderDispatch orderDispatch;

    @Override
    protected String initOrderTask() {

        for (NeedRes nr : orderHome.getInstance().getNeedReses()) {
            if (nr.getDispatches().isEmpty()) {
                //needRes = nr;
                needResHome.setId(nr.getId());
                break;
            }
        }

        orderDispatch.init(needResHome.getInstance());

        return "success";
    }


    @Override
    protected String completeOrderTask() {


        if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
            boolean allCustomerSelf = true;
            for (Dispatch dispatch : orderDispatch.getDispatchList()) {
                if (!dispatch.getDeliveryType().equals(Dispatch.DeliveryType.CUSTOMER_SELF)) {
                    allCustomerSelf = false;
                    break;
                }
            }
            if (allCustomerSelf) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "canotAllCustomerSelf");
                return "falil";
            }
        }
        needResHome.getInstance().getDispatches().addAll(orderDispatch.getDispatchList());

        needResHome.getInstance().setDispatched(true);

        if (needResHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "updateFail";
        }
    }


    public String getToastMessages() {
        StringBuffer result = new StringBuffer();
        result.append(messages.get("OrderCode") + ":" + orderHome.getInstance().getId() + "\n");

        for (Dispatch dispatch : orderDispatch.getDispatchList()) {
            result.append(dispatch.getStore().getName() + "\n");
            for (DispatchItem item : dispatch.getDispatchItemList()) {
                result.append("\t" + item.getStoreRes().getTitle(dictionary) + " ");
                result.append(item.getResCount().getMasterDisplayCount());
                result.append("(" + item.getResCount().getDisplayAuxCount() + ")");
            }
        }

        return result.toString();
    }

}
