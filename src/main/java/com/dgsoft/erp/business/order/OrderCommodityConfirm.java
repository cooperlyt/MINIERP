package com.dgsoft.erp.business.order;

import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Out;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 4:24 PM
 */
@Name("orderCommodityConfirm")
public class OrderCommodityConfirm extends OrderTaskHandle {

    @Out(value = "orderConfirmed", scope = ScopeType.CONVERSATION, required = false)
    private Boolean orderconfirmed;

    private String selectDispatchId;

    private String selectSendEmpId;

    public String getSelectDispatchId() {
        return selectDispatchId;
    }

    public void setSelectDispatchId(String selectDispatchId) {
        this.selectDispatchId = selectDispatchId;
    }

    public String getSelectSendEmpId() {
        return selectSendEmpId;
    }

    public void setSelectSendEmpId(String selectSendEmpId) {
        this.selectSendEmpId = selectSendEmpId;
    }

    public void selectSendEmployee(){
        for (NeedRes needRes: orderHome.getInstance().getNeedReses()){
            for (Dispatch dispatch: needRes.getDispatches()){
                if (dispatch.getId().equals(selectDispatchId)){
                    dispatch.setSendEmp(selectSendEmpId);
                    break;
                }
            }
        }
    }

    protected String completeOrderTask() {

        //TODO set orderconfirmed to false
        orderconfirmed = true;
        orderHome.getInstance().setResReceived(true);
        if ("updated".equals(orderHome.update())) {
            return "taskComplete";
        } else {
            return "fail";
        }

    }

}
