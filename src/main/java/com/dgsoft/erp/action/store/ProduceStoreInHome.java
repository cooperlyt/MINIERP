package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.ProductGroupSelect;
import com.dgsoft.erp.action.StockChangeHome;
import com.dgsoft.erp.model.ProductStoreIn;
import com.dgsoft.erp.model.StockChange;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;
import org.jboss.seam.faces.FacesMessages;
import org.jboss.seam.international.StatusMessage;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooper
 * Date: 10/21/13
 * Time: 10:07 PM
 * To change this template use File | Settings | File Templates.
 */
@Name("produceStoreInHome")
public class ProduceStoreInHome extends ErpEntityHome<ProductStoreIn> {

    @In
    protected FacesMessages facesMessages;

    @In(create = true)
    private StockChangeHome stockChangeHome;

    @In
    protected NumberBuilder numberBuilder;

    @In(create = true)
    private ProductGroupSelect productGroupSelect;

    @In(value = "storeInAction", create = true)
    private StoreInAction produceStoreInAction;

    @In
    protected RunParam runParam;


    public String begin() {

        getInstance().setProductGroup(productGroupSelect.getProductGroup());
        stockChangeHome.getInstance().setOperType(StockChange.StoreChangeType.PRODUCE_IN);
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            stockChangeHome.getInstance().setId("I" + numberBuilder.getDateNumber("storeInCode"));
        }
        return "ProcduceBeginStoreIn";
    }

    public String complete() {
        if (produceStoreInAction.getStoreInItems().isEmpty()) {
            facesMessages.addFromResourceBundle(StatusMessage.Severity.ERROR, "storeInNotItem");
            return null;
        }

        produceStoreInAction.storeChange(true);
        stockChangeHome.getInstance().setVerify(true);
        stockChangeHome.getInstance().setProductStoreIn(getInstance());
        getInstance().setStockChange(stockChangeHome.getReadyInstance());

        if ("persisted".equals(persist())) {
            return "stockChangeComplete";
        } else {
            return null;
        }
    }


    public String cancel() {
        clearInstance();
        return "ProcduceStoreInCancel";
    }

}
