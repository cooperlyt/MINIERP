package com.dgsoft.erp;

import org.jboss.seam.framework.EntityQuery;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 9/23/13
 * Time: 10:18 PM
 */
public class ErpEntityQuery<E> extends EntityQuery<E> {

    @Override
    protected String getPersistenceContextName() {

        return "erpEntityManager";
    }
}
