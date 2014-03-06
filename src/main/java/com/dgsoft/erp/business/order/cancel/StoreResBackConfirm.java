package com.dgsoft.erp.business.order.cancel;

import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;

/**
 * Created by cooper on 3/6/14.
 */
@Name("storeResBackConfirm")
@Scope(ScopeType.CONVERSATION)
public class StoreResBackConfirm extends CancelOrderTaskHandle{

    @Override
    protected void initCancelOrderTask() {

    }

}
