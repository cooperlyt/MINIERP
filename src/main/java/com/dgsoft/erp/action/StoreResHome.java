package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.action.store.StoreResFormatFilter;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.tools.StoreResPropertyTreeNode;
import org.jboss.seam.annotations.End;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Observer;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;
import org.richfaces.component.UITree;
import org.richfaces.event.TreeSelectionChangeEvent;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/7/13
 * Time: 5:43 PM
 */

@Name("storeResHome")
public class StoreResHome extends ErpSimpleEntityHome<StoreRes> {

    public static final String STORE_RES_CODE_RULE_PARAM_NAME = "erp.storeResRegRule";


    private String getStoreResId(Res res, Collection<Format> formatList, BigDecimal floatConvertRate, String exId) {
        List<StoreRes> storeResList = getEntityManager().createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId and storeRes.id != :exId")
                .setParameter("resId", res.getId()).setParameter("exId", (exId == null) ? "" : exId).getResultList();

        log.debug("valid res: " + res.getName() + "|convertRate :" + floatConvertRate);

        for (Format format: formatList){
            log.debug("valid format:" + format.getFormatDefine().getName() + "=" + format.getFormatValue());
        }


        log.debug("valid stores property count:" + storeResList.size());
        for (StoreRes storeRes : storeResList) {

            log.debug("very StoreRes :" + storeRes.getName() + "| format count:" + storeRes.getFormats().size() + "| covnertRate:"
                    + storeRes.getFloatConversionRate());
            if (ResHelper.sameFormat(storeRes.getFormats(), formatList)
                    && (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                    || (storeRes.getFloatConversionRate().compareTo(floatConvertRate) == 0))) {

                return storeRes.getId();
            }
        }
        return null;
    }

    public void setRes(Res res, Collection<Format> formatList, BigDecimal floatConvertRate) {

        String findStoresId = getStoreResId(res, formatList, floatConvertRate, null);
        if (findStoresId != null) {
            this.setId(findStoresId);
            getInstance();
            log.debug("setRes if found:" + findStoresId);
            return;
        }

        clearInstance();
        getInstance().setRes(res);
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            getInstance().setFloatConversionRate(floatConvertRate);
        }
        for (Format format : formatList) {
            format.setStoreRes(getInstance());
            getInstance().getFormats().add(format);
        }
    }

    public Stock getStock(Store store) {
        for (Stock stock : getInstance().getStocks()) {
            if (stock.getStore().getId().equals(store.getId())) {
                return stock;
            }
        }
        return null;
    }

    @Observer(value = "erp.resLocateSelected", create = false)
    public void resSelected(Res res) {
        if (isEditing() && !isManaged()) {
            getInstance().setRes(res);
            if (DataFormat.isEmpty(getInstance().getCode())) {
                if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                        getInstance().getFloatConversionRate() != null)
                    getInstance().setCode(resHelper.genStoreResCode(getReadyInstance()));
            }

        }
    }

    public void genCode() {
        if (getInstance().getRes() == null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResCodeCantGenForNullRes");
            return;
        }
        if (!getInstance().getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                getInstance().getFloatConversionRate() != null) {
            getInstance().setCode(resHelper.genStoreResCode(getReadyInstance()));
        } else {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeResCodeCantGenForFolatConvertRate");
        }
    }

    private boolean verifyEdit() {
        if (getInstance().getRes() == null) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeRes_res_empty_error");
            return false;
        }

        if (getInstance().getRes().getFormatDefines().size() != getInstance().getFormats().size()) {
            throw new IllegalArgumentException("format size error");
        }

        if (getStoreResId(getInstance().getRes(), getInstance().getFormats(), getInstance().getFloatConversionRate(), getInstance().getId()) != null) {

            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeRes_same_format_exists");
            return false;
        }
        return true;
    }

    @Override
    protected boolean verifyUpdateAvailable() {

        return verifyEdit();
    }

    @Override
    protected boolean verifyRemoveAvailable() {

        lastChangeRes = getInstance().getRes();
        boolean result = getInstance().getStockChangeItems().isEmpty() &&
                getInstance().getAllocationReses().isEmpty() &&
                getInstance().getOrderItems().isEmpty() &&
                getInstance().getDispatchItems().isEmpty() &&
                getInstance().getOverlyOuts().isEmpty() &&
                getInstance().getStocks().isEmpty() &&
                getInstance().getPrepareStockChanges().isEmpty();
        if (!result)
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeRes_cant_delete_error");
        return result;
    }

    @Override
    protected boolean verifyPersistAvailable() {
        boolean result = verifyEdit();
        if (result) {

        }
        return result;
    }


    @Override
    protected void initInstance() {
        super.initInstance();
        if (isIdDefined()) {
            storeResFormatFilter.selectedStoreRes(getInstance(), true);
        }
    }


    @Override
    protected boolean wire() {
        if (!isIdDefined()) {
            getInstance().getFormats().clear();

            getInstance().getFormats().addAll(storeResFormatFilter.getResFormatList());
            for (Format format : getInstance().getFormats()) {
                format.setStoreRes(getInstance());
            }
        }
        lastChangeRes = getInstance().getRes();
        return true;
    }


    @In(create = true)
    private StoreResFormatFilter storeResFormatFilter;

    @In
    private FacesMessages facesMessages;

    @In
    private ResHelper resHelper;

    private Res lastChangeRes;

    @End
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);

        Object rowData = tree.getRowData();
        clearInstance();
        if (rowData instanceof StoreResPropertyTreeNode.StoreResTreeNode) {
            setId(((StoreResPropertyTreeNode.StoreResTreeNode) rowData).getStoreRes().getId());
        }

        tree.setRowKey(storedKey);
    }

    @Observer(value = "org.jboss.seam.afterTransactionSuccess.StoreRes", create = false)
    public void saveAfter() {
        log.debug("call StoreRes Home saveAfter");
        if (lastChangeRes != null) {
            getEntityManager().refresh(lastChangeRes);
            log.debug("refresh Res:" + lastChangeRes.getName() + " count is:" + lastChangeRes.getStoreReses().size());
        }
    }
}
