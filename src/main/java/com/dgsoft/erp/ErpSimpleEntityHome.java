package com.dgsoft.erp;

import com.dgsoft.common.NamedModel;
import com.dgsoft.common.SimpleEntityHome;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/27/13
 * Time: 9:46 AM
 * To change this template use File | Settings | File Templates.
 */
public class ErpSimpleEntityHome<E extends NamedModel> extends SimpleEntityHome<E> {

    @Override
    protected String getPersistenceContextName(){
        return "erpEntityManager";
    }
}
