package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.common.system.business.BusinessCreate;
import com.dgsoft.erp.action.*;
import com.dgsoft.erp.model.*;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.jboss.seam.log.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by cooper on 2/23/14.
 */
@Name("storeResBackCreate")
@Scope(ScopeType.CONVERSATION)
public class StoreResBackCreate {

    @Logger
    private Log log;

    @DataModel("orderBackItems")
    private List<BackItem> backItems;

    @In
    private ResHelper resHelper;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private FacesMessages facesMessages;

    @In(create = true)
    private CustomerHome customerHome;

    @In(required = false)
    private CustomerAreaHome customerAreaHome;

    @In(create = true)
    private BusinessCreate businessCreate;

    @In(required = false)
    private ResHome resHome;

    @In(create=true)
    private OrderBackHome orderBackHome;

    @DataModelSelection
    private BackItem selectBackItem;

    @In(create = true)
    private ResBackDispatch resBackDispatch;

    @Create
    public void onCreate(){
        backItems = new ArrayList<BackItem>();
        orderBackHome.clearInstance();
        orderBackHome.getInstance().setOrderBackType(OrderBack.OrderBackType.PART_ORDER_BACK);
        orderBackHome.init();
    }

    private BackItem operBackItem;

    public BackItem getOperBackItem() {
        return operBackItem;
    }

    public void setOperBackItem(BackItem operBackItem) {
        this.operBackItem = operBackItem;
    }

    public void deleteItem(){
        backItems.remove(selectBackItem);
        calcBackMoney();
    }

    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BackItem item : backItems) {
            result = result.add(item.getTotalPrice());
        }
        return result;
    }

    public void addNewBackItem() {
        storeResHome.setRes(operBackItem.getRes(), operBackItem.getFormats(), operBackItem.getFloatConvertRate());
        if (storeResHome.isIdDefined()) {
            operBackItem.setStoreRes(storeResHome.getInstance());
            boolean find = false;
            for (BackItem item : backItems) {
                if (item.isSameItem(operBackItem)) {
                    find = true;
                    item.add(operBackItem);
                }
            }

            if (!find) {
                backItems.add(operBackItem);
                operBackItem.setOrderBack(orderBackHome.getInstance());
            } else {
                facesMessages.addFromResourceBundle(StatusMessage.Severity.INFO, "storeResInOrderItemMerger");
            }

            calcBackMoney();

            operBackItem = null;
            if (resHome != null) {
                resHome.clearInstance();
            }
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
        }
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        log.debug("storeResFormat selectedStoreRes Observer ");
        operBackItem = new BackItem(storeRes, resHelper.getFormatHistory(storeRes.getRes()),
                storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ? resHelper.getFloatConvertRateHistory(storeRes.getRes()) : null,
                storeRes.getRes().getResUnitByOutDefault());
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        log.debug("selectedRes selectedStoreRes Observer ");
        operBackItem = new BackItem(res, resHelper.getFormatHistory(res),
                res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ? resHelper.getFloatConvertRateHistory(res) : null,
                res.getResUnitByOutDefault());
    }

    public void calcBackMoney(){
        orderBackHome.getInstance().setMoney(getResTotalMoney().subtract(orderBackHome.getInstance().getSaveMoney()));

    }

    public String dispatchBack(){
        if (!isCanCreate()) return null;
        resBackDispatch.init(backItems);
        return "/business/startPrepare/erp/sale/BackStoreResDispatch.xhtml";
    }




    @Transactional
    public String dispatchAndCreateBack(){
        if (!resBackDispatch.isComplete()){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"dispatchNotComplete");
            return null;
        }
        orderBackHome.getInstance().setDispatched(true);
        orderBackHome.getInstance().getProductBackStoreIn().clear();
        orderBackHome.getInstance().getProductBackStoreIn().addAll(resBackDispatch.getResBackDispatcheds(orderBackHome.getInstance()));
        return createBack();
    }

    @Transactional
    public String createBack() {

        if (!isCanCreate()) return null;

        calcBackMoney();
        orderBackHome.getInstance().setCustomerOrder(null);
        if (customerHome.isIdDefined()){
            customerHome.refresh();
            orderBackHome.getInstance().setCustomer(customerHome.getInstance());
        }else{
            orderBackHome.getInstance().setCustomer(customerHome.getReadyInstance());
            orderBackHome.getInstance().getCustomer().setCustomerArea(customerAreaHome.getInstance());
        }

        orderBackHome.getInstance().getBackItems().addAll(backItems);

        return businessCreate.create();

    }

    private boolean isCanCreate() {
        if (backItems.isEmpty()){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"noneBackItemAdd");
            return false;
        }
        if (orderBackHome.getInstance().getMoney().compareTo(BigDecimal.ZERO) < 0){
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR,"backMoneyCantLessZero");
            return false;
        }
        return true;
    }


}
