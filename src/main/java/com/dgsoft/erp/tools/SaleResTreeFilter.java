package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StockChange;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:44 AM
 */
public class SaleResTreeFilter implements ResTreeFilter{


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
        return StockChange.StoreChangeType.SELL_OUT.getResTypes();
    }

    @Override
    public boolean isAddRes() {
        return true;
    }
}
