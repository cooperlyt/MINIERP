package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.store.StoreChangeHelper;
import com.dgsoft.erp.model.*;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/7/13
 * Time: 5:43 PM
 */

@Name("storeResHome")
public class StoreResHome extends ErpEntityHome<StoreRes> {

    public static final String STORE_RES_CODE_RULE_PARAM_NAME = "erp.storeResRegRule";

    public void setRes(Res res, Collection<Format> formatList, BigDecimal floatConvertRate) {

        List<StoreRes> storeResList = getEntityManager().createQuery("select storeRes from StoreRes storeRes where storeRes.res.id = :resId").setParameter("resId", res.getId()).getResultList();
        for (StoreRes storeRes : storeResList) {

            log.debug("setRes:" + storeRes + "|" + res + "|" + res.getUnitGroup() + "|" + res.getUnitGroup().getType() + "|"
                    + storeRes.getFloatConversionRate());
            if (StoreChangeHelper.sameFormat(storeRes.getFormats(), formatList)
                    && (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                    || (storeRes.getFloatConversionRate().compareTo(floatConvertRate) == 0))) {
                this.setId(storeRes.getId());
                getInstance();
                return;
            }
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

}
