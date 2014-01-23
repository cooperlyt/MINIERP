package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StockChange;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:36 AM
 */
public class ProduceInResTreeFilter implements ResTreeFilter{

    @Override
    public StoreResAddType storesAddType() {
        return StoreResAddType.NOT_ADD;
    }

    @Override
    public boolean containDisable() {
        return false;
    }

    @Override
    public EnumSet<ResCategory.ResType> getCategoryTypes() {
        return StockChange.StoreChangeType.PRODUCE_IN.getResTypes();
    }

    @Override
    public boolean isAddRes() {
        return true;
    }

    @Override
    public boolean expandedDefault() {
        return false;
    }
}
