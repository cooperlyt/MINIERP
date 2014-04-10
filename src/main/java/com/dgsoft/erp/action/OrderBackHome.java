package com.dgsoft.erp.action;

import com.dgsoft.common.SetLinkList;
import com.dgsoft.common.exception.ProcessCreatePrepareException;
import com.dgsoft.common.jbpm.BussinessProcessUtils;
import com.dgsoft.common.system.action.BusinessDefineHome;
import com.dgsoft.common.system.business.StartData;
import com.dgsoft.common.system.model.BusinessDefine;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.model.BackDispatch;
import com.dgsoft.erp.model.BackItem;
import com.dgsoft.erp.model.OrderBack;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.security.Credentials;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 12/18/13
 * Time: 2:52 PM
 */
@Name("orderBackHome")
public class OrderBackHome extends ErpEntityHome<OrderBack> {

    protected List<BackItem> backItems;

    @Override
    protected void initInstance() {
        super.initInstance();
        backItems = new SetLinkList<BackItem>(getInstance().getBackItems());
    }

    public boolean needStoreIn(String storeId) {

        for (BackDispatch BACKDISPATCH : getInstance().getBackDispatchs()) {
            if (BACKDISPATCH.getStore().getId().equals(storeId)) {
                return true;
            }
        }
        return false;

    }

    public List<BackItem> getBackItems() {
        getInstance();
        return backItems;
    }

    public boolean isNeedBackMoney() {
        return getInstance().getMoney().compareTo(BigDecimal.ZERO) > 0;
    }

    public boolean isNeedBackRes() {
        return !getInstance().getBackItems().isEmpty();
    }


    public BigDecimal getResTotalMoney() {
        BigDecimal result = BigDecimal.ZERO;
        for (BackItem item : getInstance().getBackItems()) {
            if (item.getTotalMoney() != null) {
                result = result.add(item.getTotalMoney());
            }
        }
        return result;
    }

    public void calcBackMoney() {
        getInstance().setMoney(getResTotalMoney().subtract(getInstance().getSaveMoney()));
    }

}
