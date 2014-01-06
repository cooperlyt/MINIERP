package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.erp.action.DispatchHome;
import com.dgsoft.erp.model.Dispatch;
import com.dgsoft.erp.model.NeedRes;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.OverlyOut;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.security.Credentials;
import sun.plugin2.os.windows.OVERLAPPED;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

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

    private List<OverlyOut> noConfirmOverlys;

    @DataModel
    private List<OrderItem> overlyOrderItems;

    @DataModelSelection
    private OrderItem selectOverlyOrderItem;

    private OverlyOut selectOverly;

    private OrderItem editingOrderItem;

    public List<OverlyOut> getNoConfirmOverlys() {
        return noConfirmOverlys;
    }

    public String getOverlyOutId() {

       if (selectOverly == null){
           return null;
       }else{
           return selectOverly.getId();
       }
    }

    public void setOverlyOutId(String overlyOutId) {
        if (StringUtil.isEmpty(overlyOutId)){
            selectOverly = null;
            return;
        }

        for (OverlyOut overly : noConfirmOverlys) {
            if (overly.getId().equals(overlyOutId)) {
                selectOverly = overly;
                return;
            }
        }
        selectOverly = null;
    }

    public void autoConfirmAll(){

    }

    public void beginConfirmOverly() {
        editingOrderItem = new OrderItem(dispatchHome.getInstance().getNeedRes(),
                selectOverly.getStoreRes(),BigDecimal.ZERO);
    }

    public void saveOverlyToOrderItem() {

    }

    public OverlyOut getSelectedOverly() {

        return null;
    }

    protected String initOrderTask() {
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

                        overlyOrderItems = new ArrayList<OrderItem>();
                        noConfirmOverlys = new ArrayList<OverlyOut>();
                        for (OverlyOut overly : dispatchHome.getInstance().getOverlyOuts()) {
                            if (!overly.isAddTo()) {
                                noConfirmOverlys.add(overly);
                            }
                        }


                        switch (dispatchHome.getInstance().getDeliveryType()) {
                            case FULL_CAR_SEND:
                                //dispatchHome.getInstance().getExpressCar().
                                //        setCarCode(dispatchHome.getInstance().getExpressCar().getExpressDriver().getCarCode());
                                break;

                            case SEND_TO_DOOR:
                                dispatchHome.getInstance().getProductToDoor().
                                        setToDoorDriver(dispatchHome.getInstance().getProductToDoor().getCars().getDefaultDriver());
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

    protected String completeOrderTask() {

        dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
        dispatchHome.getInstance().setSendEmp(credentials.getUsername());
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "fail";
        }


    }


}
