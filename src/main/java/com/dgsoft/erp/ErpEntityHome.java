package com.dgsoft.erp;

import com.dgsoft.common.EntityHomeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/23/13
 * Time: 10:17 PM
 */
public class ErpEntityHome<E> extends EntityHomeAdapter<E> {

    @Override
    protected String getPersistenceContextName(){
        return "erpEntityManager";
    }
}
