package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.erp.action.DispatchHome;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/13/13
 * Time: 4:32 PM
 */
@Name("orderStoreOut")
public class OrderStoreOut extends OrderTaskHandle {

    private final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    private String storeId;

    @Override
    protected String initOrderTask(){

        storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null){
            throw new ProcessDefineException("Order Store out store ID not Define");
        }

        for (NeedRes needRes: orderHome.getInstance().getNeedReses()){
            if (needRes.isDispatched()){
                for (Dispatch dispatch: needRes.getDispatches()){
                    if (dispatch.getStore().getId().equals(storeId) &&
                            dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_COMPLETE)){
                        dispatchHome.setId(dispatch.getId());
                        return "success";
                    }
                }
            }
        }



        return "fail";
    }


}
