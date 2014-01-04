package com.dgsoft.erp;

import com.dgsoft.common.EntityQueryAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/23/13
 * Time: 10:18 PM
 */
public class ErpEntityQuery<E> extends EntityQueryAdapter<E> {

    @Override
    protected String getPersistenceContextName() {

        return "erpEntityManager";
    }
}
