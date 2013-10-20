package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreArea;
import com.dgsoft.erp.model.api.StockChangeModel;
import org.jboss.seam.annotations.Begin;
import org.jboss.seam.annotations.FlushModeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.datamodel.DataModel;
import org.jboss.seam.annotations.datamodel.DataModelSelection;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/18/13
 * Time: 11:28 AM
 */
public abstract class StoreInAction<E extends StockChangeModel> extends StoreChangeHelper<E> implements StoreChangeAction {

    @In
    private RunParam runParam;

    @In
    private NumberBuilder numberBuilder;

    @DataModel(value = "storeInItems")
    private List<StoreInItem> storeInItems = new ArrayList<StoreInItem>();

    @DataModelSelection
    private StoreInItem selectedStoreInItem;

    @Override
    public String beginStoreChange() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            stockChangeHome.setCode(numberBuilder.getDateNumber("storeInCode"));
        }
        return "storeIn";
    }

    public static class StoreInItem {

        private StoreArea storeArea;

        private Res res;

        private List<Format> formats;

        private BigDecimal count;

        public StoreInItem(StoreArea storeArea, Res res, List<Format> formats, BigDecimal count) {
            this.storeArea = storeArea;
            this.res = res;
            this.formats = formats;
            this.count = count;
        }

        public StoreArea getStoreArea() {
            return storeArea;
        }

        public void setStoreArea(StoreArea storeArea) {
            this.storeArea = storeArea;
        }

        public Res getRes() {
            return res;
        }

        public void setRes(Res res) {
            this.res = res;
        }

        public List<Format> getFormats() {
            return formats;
        }

        public BigDecimal getCount() {
            return count;
        }

        public void setCount(BigDecimal count) {
            this.count = count;
        }

        public boolean sameItem(StoreInItem storeInItem) {
            return (storeArea.getId().equals(storeInItem.getStoreArea().getId()) &&
                    res.getId().equals(storeInItem.getRes().getId()) &&
                    ResHelper.sameFormat(storeInItem.getFormats(), formats)
            );
        }
    }

}
