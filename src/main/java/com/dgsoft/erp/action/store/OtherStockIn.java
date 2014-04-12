package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.action.OtherStoreChangeHome;
import com.dgsoft.erp.model.StockChange;
import com.dgsoft.erp.model.StockChangeItem;
import com.dgsoft.erp.model.StoreChange;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.annotations.Transactional;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.international.StatusMessage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 12/22/13
 * Time: 8:43 PM
 * To change this template use File | Settings | File Templates.
 */

@Name("otherStockIn")
@Scope(ScopeType.CONVERSATION)
public class OtherStockIn extends StoreInAction {

    @In
    protected NumberBuilder numberBuilder;

    @In
    private RunParam runParam;


    @In
    private OtherStoreChangeHome otherStoreChangeHome;

    @Transactional
    public String begin() {


        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            stockChangeHome.getInstance().setId("I" + numberBuilder.getDateNumber("storeInCode"));
        }
        return "BeginOtherStockIn";
    }

    @Transactional
    public String complete() {
        if (storeInItems.isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeInNotItem");
            return null;
        }
        super.storeChange(true);
        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setStoreChange(otherStoreChangeHome.getInstance());
        otherStoreChangeHome.getInstance().setStockChange(stockChangeHome.getReadyInstance());

        if ("persisted".equals(otherStoreChangeHome.persist())) {
            return "OtherStockChangeComplete";
        } else {
            return null;
        }
    }


    public String cancel() {
        stockChangeHome.clearInstance();
        return "OtherStockChangeCancel";
    }
}
