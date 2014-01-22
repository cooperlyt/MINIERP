package com.dgsoft.erp.tools;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 1/22/14
 * Time: 10:45 AM
 */
public class SaleStoreResTreeFilter extends SaleResTreeFilter{

    @Override
    public StoreResAddType storesAddType() {
        return StoreResAddType.PROPERTY_ADD;
    }
}
