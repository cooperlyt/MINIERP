package com.dgsoft.erp.action;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.ErpSimpleEntityHome;
import com.dgsoft.erp.action.store.StoreChangeHelper;
import com.dgsoft.erp.action.store.StoreResFormatFilter;
import com.dgsoft.erp.model.*;
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


    private String getStoreResId(Res res, Collection<Format> formatList, BigDecimal floatConvertRate) {
        List<StoreRes> storeResList = getEntityManager().createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId").setParameter("resId", res.getId()).getResultList();
        for (StoreRes storeRes : storeResList) {

            log.debug("setRes:" + storeRes + "|" + res + "|" + res.getUnitGroup() + "|" + res.getUnitGroup().getType() + "|"
                    + storeRes.getFloatConversionRate());
            if (StoreChangeHelper.sameFormat(storeRes.getFormats(), formatList)
                    && (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                    || (storeRes.getFloatConversionRate().compareTo(floatConvertRate) == 0))) {

                return storeRes.getId();
            }
        }
        return null;
    }

    public void setRes(Res res, Collection<Format> formatList, BigDecimal floatConvertRate) {

        String findStoresId = getStoreResId(res, formatList, floatConvertRate);
        if (findStoresId != null) {
            this.setId(findStoresId);
            getInstance();
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
            if (StringUtil.isEmpty(getInstance().getCode())) {
                getInstance().setCode(
                        res.getCode() + "-" +
                                numberBuilder.getNumber("erp.storeResCode." + res.getCode()));
            }

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


        return true;
    }

    @Override
    protected boolean verifyUpdateAvailable() {

        return verifyEdit();
    }

    @Override
    protected boolean verifyRemoveAvailable() {


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
            if (getStoreResId(getInstance().getRes(), getInstance().getFormats(), getInstance().getFloatConversionRate()) != null) {

                facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeRes_same_format_exists");
                return false;
            }
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
        return true;
    }


    @In(create = true)
    private StoreResFormatFilter storeResFormatFilter;

    @In
    private FacesMessages facesMessages;

    @In
    protected NumberBuilder numberBuilder;

    @End
    public void selectionChanged(TreeSelectionChangeEvent selectionChangeEvent) {
        List<Object> selection = new ArrayList<Object>(selectionChangeEvent.getNewSelection());
        Object currentSelectionKey = selection.get(0);
        UITree tree = (UITree) selectionChangeEvent.getSource();

        Object storedKey = tree.getRowKey();
        tree.setRowKey(currentSelectionKey);

        Object rowData = tree.getRowData();
        clearInstance();
        if (rowData instanceof ResCategoryHome.StoreResNode) {
            setId(((ResCategoryHome.StoreResNode) rowData).getStoreRes().getId());
        }

        tree.setRowKey(storedKey);
    }
}
