package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 8:53 AM
 */
public interface ResTreeFilter {

    public enum StoreResAddType{

        NOT_ADD,PROPERTY_ADD,LIST_ADD;
    }


    public abstract StoreResAddType storesAddType();

    public abstract boolean containDisable();

    public abstract EnumSet<ResCategory.ResType> getCategoryTypes();

    public abstract boolean isAddRes();


}
