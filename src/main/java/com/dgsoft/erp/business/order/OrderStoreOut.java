package com.dgsoft.erp.business.order;

import com.dgsoft.common.exception.ProcessDefineException;
import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.business.TaskDescription;
import com.dgsoft.erp.action.DispatchHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.international.StatusMessage;

import java.math.BigDecimal;
import java.util.Date;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 11/13/13
 * Time: 4:32 PM
 */
@Name("orderStoreOut")
public class OrderStoreOut extends OrderTaskHandle {

    public final static String TASK_STORE_ID_KEY = "storeId";

    @In
    private TaskDescription taskDescription;

    @In(create = true)
    private DispatchHome dispatchHome;

    @In
    private ResHelper resHelper;

    @In
    protected NumberBuilder numberBuilder;

    @In
    private org.jboss.seam.security.Credentials credentials;

    //private String storeId;

    private String memo;

    private Date storeOutDate;

    public Date getStoreOutDate() {
        return storeOutDate;
    }

    public void setStoreOutDate(Date storeOutDate) {
        this.storeOutDate = storeOutDate;
    }

    public String getMemo() {
        return memo;
    }

    public void setMemo(String memo) {
        this.memo = memo;
    }

    @Override
    protected String completeOrderTask() {
        for (DispatchItem item : dispatchHome.getInstance().getDispatchItems()) {
            if (!item.isEnough()) {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,
                        "orderStockNotEnoughCantStoreOut",
                        resHelper.generateStoreResTitle(item.getStoreRes()),
                        item.getResCount().getMasterDisplayCount() + "(" +
                                item.getResCount().getDisplayAuxCount() + ")",
                        item.getStockCount().getMasterDisplayCount() + "(" +
                                item.getStockCount().getDisplayAuxCount() + ")",
                        item.getDisparity().getMasterDisplayCount() + "(" +
                                item.getDisparity().getDisplayAuxCount() + ")");
                return "storeNotEnough";
            }
        }

        dispatchHome.getInstance().setStockChange(
                new StockChange(orderHome.getInstance().getId() + "-" + numberBuilder.getNumber("storeInCode"),dispatchHome.getInstance().getStore(), storeOutDate,
                        credentials.getUsername(), StockChange.StoreChangeType.SELL_OUT, memo,true));

        for (DispatchItem item : dispatchHome.getInstance().getDispatchItems()) {
            //TODO noConvertRate Unit
            StockChangeItem stockChangeItem = new StockChangeItem(dispatchHome.getInstance().getStockChange(),
                    item.getStock(), item.getResCount().getMasterCount(), true);
            dispatchHome.getInstance().getStockChange().getStockChangeItems()
                    .add(stockChangeItem);
            item.getStock().setCount(stockChangeItem.getAfterCount());
            //TODO UseSelectBatch
            //TODO StoreArea Count subtract
            BigDecimal count = stockChangeItem.getCount();
            for (BatchStoreCount bsc : item.getStock().getBatchStoreCountList()) {
                if (bsc.getCount().compareTo(count) >= 0) {
                    bsc.setCount(bsc.getCount().subtract(count));
                    count = BigDecimal.ZERO;

                    break;
                } else {
                    count = count.subtract(bsc.getCount());
                    bsc.setCount(BigDecimal.ZERO);

                    //TODO Del Batch StoreCount if not Default; but save Batch info
                }
            }
            if (count.compareTo(BigDecimal.ZERO) != 0) {
                throw new IllegalStateException("batch count error, not equals stock count");
            }
        }

        boolean allStoreOut = true;
        for(Dispatch dispatch:  orderHome.getMasterNeedRes().getDispatches()){
            if (!dispatch.getId().equals(dispatchHome.getInstance().getId())){
                if (dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_COMPLETE)){
                    allStoreOut = false;
                    break;
                }
            }
        }

        if (allStoreOut){
            orderHome.getInstance().setAllStoreOut(allStoreOut);
        }

        if (dispatchHome.getInstance().getDeliveryType().equals(Dispatch.DeliveryType.CUSTOMER_SELF)) {
            dispatchHome.getInstance().setState(Dispatch.DispatchState.ALL_COMPLETE);
            dispatchHome.getInstance().setSendTime(storeOutDate);
            dispatchHome.getInstance().setFare(BigDecimal.ZERO);
        } else {
            dispatchHome.getInstance().setState(Dispatch.DispatchState.DISPATCH_STORE_OUT);
        }
        if (dispatchHome.update().equals("updated")) {
            return "taskComplete";
        } else {
            return "updateFail";
        }


    }

    @Override
    protected String initOrderTask() {

        String storeId = taskDescription.getValue(TASK_STORE_ID_KEY);
        if (storeId == null) {
            throw new ProcessDefineException("Order Store out store ID not Define");
        }

        for (NeedRes needRes : orderHome.getInstance().getNeedReses()) {
            if (needRes.isDispatched()) {
                for (Dispatch dispatch : needRes.getDispatches()) {
                    if (dispatch.getStore().getId().equals(storeId) &&
                            dispatch.getState().equals(Dispatch.DispatchState.DISPATCH_COMPLETE)) {
                        dispatchHome.setId(dispatch.getId());

                        return "success";
                    }
                }
            }
        }


        return "fail";
    }


}
