package com.dgsoft.erp.business.order.cancel;

import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
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
    private List<BackItem> backItems = new ArrayList<BackItem>();

    @In
    private ResHelper resHelper;

    @In(create = true)
    private StoreResHome storeResHome;

    @In
    private FacesMessages facesMessages;

    @DataModelSelection
    private BackItem selectBackItem;

    private BackItem operBackItem;

    public BackItem getOperBackItem() {
        return operBackItem;
    }

    public void setOperBackItem(BackItem operBackItem) {
        this.operBackItem = operBackItem;
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

            if (!find)
                backItems.add(operBackItem);

            log.debug("backitem is added!");

        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "orderStoreResNotExists");
        }
    }

    private void createNewBackItem(Res res, BigDecimal floatConvertRate) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            if (floatConvertRate == null) {
                operBackItem = new BackItem(res, resHelper.getFormatHistory(res), resHelper.getFloatConvertRateHistory(res), res.getResUnitByOutDefault());
            } else {
                operBackItem = new BackItem(res, resHelper.getFormatHistory(res), resHelper.getFloatConvertRateHistory(res), floatConvertRate, res.getResUnitByOutDefault());
            }
        } else {
            operBackItem = new BackItem(res, resHelper.getFormatHistory(res), res.getResUnitByOutDefault());
        }

    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        log.debug("storeResFormat selectedStoreRes Observer ");
        createNewBackItem(storeRes.getRes(), storeRes.getFloatConversionRate());
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        log.debug("selectedRes selectedStoreRes Observer ");
        createNewBackItem(res, null);
    }

    public String createBack() {
        return null;
    }


}
