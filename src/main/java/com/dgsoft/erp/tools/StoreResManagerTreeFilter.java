package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 9:55 AM
 */
public class StoreResManagerTreeFilter implements ResTreeFilter{


    @Override
    public StoreResAddType storesAddType() {
        return StoreResAddType.PROPERTY_ADD;
    }

    @Override
    public boolean containDisable() {
        return true;
    }

    @Override
    public EnumSet<ResCategory.ResType> getCategoryTypes() {
        return EnumSet.allOf(ResCategory.ResType.class);
    }

    @Override
    public boolean isAddRes() {
        return true;
    }

    @Override
    public boolean expandedDefault() {
        return true;
    }
}
