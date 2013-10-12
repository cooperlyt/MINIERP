package com.dgsoft.common.system;

import com.dgsoft.common.EntityHomeAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/8/13
 * Time: 10:27 AM
 */
public class SystemEntityHome<E> extends EntityHomeAdapter<E> {

    @Override
    protected String getPersistenceContextName(){
        return "systemEntityManager";
    }
}
