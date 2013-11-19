package com.dgsoft.erp;

import com.dgsoft.common.EntityQueryAdapter;
import org.jboss.seam.framework.EntityQuery;

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
