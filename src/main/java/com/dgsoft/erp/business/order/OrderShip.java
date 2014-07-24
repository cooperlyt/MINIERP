package com.dgsoft.erp.business.order;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.jbpm.TaskDescription;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/15/13
 * Time: 10:33 AM
 */
@Name("orderShip")
public class OrderShip extends OrderShipTaskHandle {

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    private boolean inputDetails;

    @In(create = true)
    private TransCorpHome transCorpHome;

    @In(create = true)
    private CarsHome carsHome;

    public boolean isInputDetails() {
        return inputDetails;
    }

    public void setInputDetails(boolean inputDetails) {
        this.inputDetails = inputDetails;
    }

    @Override
    protected void initOrderTask() {
        String storeId = taskDescription.getStringValue(OrderStoreOut.TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }
        NeedRes needRes = orderHome.getLastNeedRes();

        for (Dispatch dispatch : needRes.getDispatches()) {
            if (dispatch.getStore().getId().equals(storeId) &&
                    dispatch.isStoreOut()) {
                dispatchHome.setId(dispatch.getId());
                if (needRes.isFareByCustomer() || !dispatchHome.getInstance().getDeliveryType().isHaveFare() || dispatchHome.getInstance().getFare() == null) {
                    dispatchHome.getInstance().setFare(BigDecimal.ZERO);
                }

                switch (dispatchHome.getInstance().getDeliveryType()) {
                    case FULL_CAR_SEND:
                    case EXPRESS_SEND:
                        if (dispatchHome.getInstance().getTransCorp() != null) {
                            transCorpHome.setId(dispatchHome.getInstance().getTransCorp().getId());
                        } else {
                            transCorpHome.clearInstance();
                        }
                        break;

                    case SEND_TO_DOOR:
                        if (dispatchHome.getInstance().getCar() != null) {
                            carsHome.setId(dispatchHome.getInstance().getCar().getId());
                        } else {
                            carsHome.clearInstance();
                        }
                        break;
                }

            }
        }
    }

    @Override
    protected String completeOrderTask() {
        if (dispatchHome.getInstance().getSendTime().compareTo(DataFormat.getTodayLastTime()) > 0) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "DateIsFuture", DateFormat.getDateInstance(DateFormat.MEDIUM).format(dispatchHome.getInstance().getSendTime()) );
            return null;
        }

//        if (dispatchHome.getInstance().getSendTime().compareTo(DataFormat.halfTime(orderHome.getInstance().getCreateDate())) < 0){
//            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "DateIsFuture",
//                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(dispatchHome.getInstance().getSendTime()),
//                    DateFormat.getDateInstance(DateFormat.MEDIUM).format(orderHome.getInstance().getCreateDate()));
//            return null;
//        }

        if (inputDetails) {
            switch (dispatchHome.getInstance().getDeliveryType()) {
                case FULL_CAR_SEND:
                case EXPRESS_SEND:
                    if (transCorpHome.isIdDefined()) {
                        dispatchHome.getInstance().setTransCorp(transCorpHome.getInstance());
                    } else {
                        dispatchHome.getInstance().setTransCorp(transCorpHome.getReadyInstance());
                    }
                    break;

                case SEND_TO_DOOR:
                    dispatchHome.getInstance().setCar(carsHome.getInstance());
                    break;
            }
        }
        //dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);

        dispatchHome.getInstance().setDelivered(true);
        calcStoreResCompleted(dispatchHome.getInstance().getSendTime());
        if (dispatchHome.update().equals("updated")) {

            return super.completeOrderTask();
        } else {
            return null;
        }
    }


    private void calcStoreResCompleted(Date shipDate) {
        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            for (Dispatch dispatch: needRes.getDispatches()){
                if (!dispatch.getId().equals(dispatchHome.getInstance().getId()) && !dispatch.isDelivered()){
                    return;
                }
            }

            for (OrderItem item : needRes.getOrderItems()) {
                if (!item.getStatus().equals(OrderItem.OrderItemStatus.COMPLETED)) {
                    orderHome.getInstance().setAllStoreOut(false);
                    return;
                }
            }
            for (Dispatch dispatch : needRes.getDispatches()) {
                if (dispatch.isHaveNoOutOweItem()) {
                    orderHome.getInstance().setAllStoreOut(false);
                    return;
                }
            }

        }
        shipComplete(shipDate);

    }


}
