package com.dgsoft.common.system;

import com.dgsoft.common.EntityQueryAdapter;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 5/9/13
 * Time: 11:33 AM
 */
public class SystemEntityQuery<E> extends EntityQueryAdapter<E> {

    @Override
    protected String getPersistenceContextName() {

        return "systemEntityManager";
    }
}
