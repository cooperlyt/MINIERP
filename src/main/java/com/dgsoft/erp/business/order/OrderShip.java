package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.erp.action.DispatchHome;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 10:33 AM
 */
@Name("orderShip")
public class OrderShip extends OrderTaskHandle {

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    @In
    private Credentials credentials;

    protected String initOrderTask(){
        String storeId = taskDescription.getValue(OrderStoreOut.TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }

        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            if (needRes.isDispatched()) {
                for (Dispatch dispatch : needRes.getDispatches()) {
                    if (dispatch.getStore().getId().equals(storeId) &&
                            dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_STORE_OUT)) {
                        dispatchHome.setId(dispatch.getId());

                        switch (dispatchHome.getInstance().getDeliveryType()){
                            case FULL_CAR_SEND:
                                dispatchHome.getInstance().getExpressCar().
                                        setCarCode(dispatchHome.getInstance().getExpressCar().getExpressDriver().getCarCode());
                                break;

                            case SEND_TO_DOOR:
                                dispatchHome.getInstance().getProductToDoor().
                                        setEmployeeId(dispatchHome.getInstance().getProductToDoor().getCars().getEmployeeId());
                                break;

                        }

                        dispatchHome.getInstance().setFare(BigDecimal.ZERO);
                        return "success";
                    }
                }
            }
        }


        return "fail";
    }

    protected String completeOrderTask(){

        dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
        dispatchHome.getInstance().setSendEmp(credentials.getUsername());
        if (dispatchHome.update().equals("updated")){
            return "taskComplete";
        }else{
            return "fail";
        }


    }


}
