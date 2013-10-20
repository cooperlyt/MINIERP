package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.AuthenticationInfo;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.annotations.In;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/20/13
 * Time: 7:32 PM
 */
public abstract class StoreChangeHelper<E extends StockChangeModel> extends ErpEntityHome<E> implements StoreChangeAction {

    public abstract String beginStoreChange();

    @In(create = true)
    protected StockChangeHome stockChangeHome;

    @In
    private AuthenticationInfo authInfo;

    @Override
    public String begin() {
        stockChangeHome.getInstance().setOperEmp(authInfo.getLoginEmployee().getId());
        return beginStoreChange();
    }
}
