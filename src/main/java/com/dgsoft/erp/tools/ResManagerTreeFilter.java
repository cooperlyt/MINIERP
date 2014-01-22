package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:08 AM
 */
public class ResManagerTreeFilter implements ResTreeFilter{

    @Override
    public StoreResAddType storesAddType() {
        return StoreResAddType.NOT_ADD;
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

}
