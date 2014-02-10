package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.helper.ActionExecuteState;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.common.utils.math.BigDecimalFormat;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Logger;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

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
    protected String initOrderTask() {
        String storeId = taskDescription.getValue(OrderStoreOut.TASK_STORE_ID_KEY);
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

                return "success";
            }

        }


        return "fail";
    }


    @Override
    protected String completeOrderTask() {

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
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "fail";
        }


    }


}
