package com.dgsoft.erp;

import com.dgsoft.common.SimpleData;
import com.dgsoft.common.SimpleDataHome;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 9:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class ErpSimpleDataHome<E extends SimpleData> extends SimpleDataHome<E>{

    @Override
    protected String getPersistenceContextName(){
        return "erpEntityManager";
    }
}
