package com.dgsoft.erp.tools;

import com.dgsoft.erp.model.ResCategory;

import java.util.EnumSet;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:40 AM
 */
public class ProduceInStoreResTreeFilter extends ProduceInResTreeFilter {


    @Override
    public StoreResAddType storesAddType() {
        return StoreResAddType.PROPERTY_ADD;
    }

}
