package com.dgsoft.erp.business.order;

import com.dgsoft.common.system.DictionaryWord;
import com.dgsoft.erp.action.NeedResHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.CustomerOrder;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.DispatchItem;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

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

    @In
    private ResHelper resHelper;


    @In(create = true)
    private NeedResHome needResHome;

    @In(create = true)
    private OrderDispatch orderDispatch;

    @Override
    protected void initOrderTask() {

        for (NeedRes nr : orderHome.getInstance().getNeedReses()) {
            if (nr.getDispatches().isEmpty()) {
                //needRes = nr;
                needResHome.setId(nr.getId());
                break;
            }
        }

        orderDispatch.init(needResHome.getInstance().getOrderItems());

    }


    @Override
    protected String completeOrderTask() {


        if (orderHome.getInstance().getPayType().equals(CustomerOrder.OrderPayType.EXPRESS_PROXY)) {
            boolean allCustomerSelf = true;
            for (Dispatch dispatch : orderDispatch.getDispatchList(needResHome.getInstance())) {
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
        needResHome.getInstance().getDispatches().addAll(orderDispatch.getDispatchList(needResHome.getInstance()));

        needResHome.getInstance().setStatus(NeedRes.NeedResStatus.DISPATCHED);

        if (needResHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "updateFail";
        }
    }


    public String getToastMessages() {
        StringBuffer result = new StringBuffer();
        result.append(messages.get("OrderCode") + ":" + orderHome.getInstance().getId() + "\n");

        result.append(messages.get("Customer") + ":" + orderHome.getInstance().getCustomer().getName());
        result.append("\n");
        result.append(messages.get("order_field_reveiveContact") + ":" + needResHome.getInstance().getReceivePerson());
        result.append(" ");
        result.append(messages.get("order_field_reveiveTel") + " " + needResHome.getInstance().getReceiveTel());
        result.append("\n");
        result.append(messages.get("address") + ":" +  needResHome.getInstance().getAddress());
        result.append("\n");

        for (Dispatch dispatch : orderDispatch.getDispatchList(needResHome.getInstance())) {
            result.append(dispatch.getStore().getName() + "\n");
            for (DispatchItem item : dispatch.getDispatchItemList()) {
                result.append("\t" + resHelper.generateStoreResTitle(item.getStoreRes()) + " ");
                result.append(item.getDisplayMasterCount());
                result.append("(" + item.getDisplayAuxCount() + ")");
            }
        }

        return result.toString();
    }

}
