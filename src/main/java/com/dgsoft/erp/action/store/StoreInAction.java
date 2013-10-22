package com.dgsoft.erp.action.store;

import com.dgsoft.common.system.NumberBuilder;
import com.dgsoft.common.system.RunParam;
import com.dgsoft.erp.ErpEntityHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.ResLocateHome;
import com.dgsoft.erp.action.StoreResFormatFilter;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreArea;
import com.dgsoft.erp.model.UnitGroup;
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
    protected RunParam runParam;

    @In
    protected NumberBuilder numberBuilder;

    @In
    protected StoreResFormatFilter storeResFormatFilter;

    @In
    protected ResLocateHome resLocateHome;

    @DataModel(value = "storeInItems")
    protected List<StoreInItem> storeInItems = new ArrayList<StoreInItem>();

    @DataModelSelection
    protected StoreInItem selectedStoreInItem;

    @Override
    public String beginStoreChange() {
        if (runParam.getBooleanParamValue("erp.autoGenerateStoreInCode")) {
            getInstance().setId(numberBuilder.getDateNumber("storeInCode"));
        }
        return "storeIn";
    }

    @Override
    public void addItem() {

        StoreInItem newItem = null;
        switch (storeResFormatFilter.getRes().getUnitGroup().getType()){
            case NO_CONVERT:
                newItem = new StoreInItem(storeResFormatFilter.getRes(),
                        storeResFormatFilter.getResFormatList(),
                        storeResFormatFilter.getCount(),
                        storeResFormatFilter.getNoConvertCountList());
                break;
            case FIX_CONVERT:
                newItem = new StoreInItem(storeResFormatFilter.getRes(),
                        storeResFormatFilter.getResFormatList(),
                        storeResFormatFilter.getFixConvertMasterCount());
                break;
            case FLOAT_CONVERT:
                newItem = new StoreInItem(storeResFormatFilter.getRes(),
                        storeResFormatFilter.getResFormatList(),
                        storeResFormatFilter.getFloatConvertRate());
                break;
        }


        if (newItem == null){
            throw new IllegalArgumentException("no UNIT type");
        }

        for (StoreInItem storeInItem : storeInItems) {
            if (storeInItem.sameItem(newItem)) {
                storeInItem.merger(newItem);
                newItem = null;
                break;
            }
        }

        if (newItem != null) {
            storeInItems.add(newItem);
        }

        storeResFormatFilter.clearCount();
        resLocateHome.clearInstance();
    }

    public static class StoreInItem {

        //TODO batch

        //TODO storeaArea

        private Res res;

        private List<Format> formats;

        private BigDecimal count;

        private BigDecimal floatConvertRate;

        private List<StoreResFormatFilter.NoConvertAuxCount> noConvertAuxCount;

        public StoreInItem(Res res, List<Format> formats, BigDecimal count) {
            this.res = res;
            this.formats = formats;
            this.count = count;
        }


        public StoreInItem(Res res, List<Format> formats, BigDecimal count, BigDecimal floatConvertRate) {
            this.res = res;
            this.formats = formats;
            this.count = count;
            this.floatConvertRate = floatConvertRate;
        }

        public StoreInItem(Res res, List<Format> formats, BigDecimal count, List<StoreResFormatFilter.NoConvertAuxCount> noConvertAuxCount) {
            this.res = res;
            this.formats = formats;
            this.count = count;
            this.noConvertAuxCount = noConvertAuxCount;
        }

        public void merger(StoreInItem storeInItem){
           if (! sameItem(storeInItem)){
               throw new IllegalArgumentException("not same storeInItem can't merger");
           }
           if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.NO_CONVERT)){
               addCount(storeInItem.getCount(),storeInItem.getNoConvertAuxCount());
           }else{
               addCount(storeInItem.getCount());
           }

        }

        private void addCount(BigDecimal count){
            this.count.add(count);
        }

        private void addCount(BigDecimal count, List<StoreResFormatFilter.NoConvertAuxCount> auxCounts){
            addCount(count);
            for (StoreResFormatFilter.NoConvertAuxCount auxCount: auxCounts){
                for (StoreResFormatFilter.NoConvertAuxCount srcAuxCount: noConvertAuxCount){
                    if (auxCount.getResUnit().equals(srcAuxCount.getResUnit())){
                        srcAuxCount.setCount(srcAuxCount.getCount().add(auxCount.getCount()));
                    }
                }
            }
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

        public void setFormats(List<Format> formats) {
            this.formats = formats;
        }

        public BigDecimal getFloatConvertRate() {
            return floatConvertRate;
        }

        public void setFloatConvertRate(BigDecimal floatConvertRate) {
            this.floatConvertRate = floatConvertRate;
        }

        public List<StoreResFormatFilter.NoConvertAuxCount> getNoConvertAuxCount() {
            return noConvertAuxCount;
        }

        public void setNoConvertAuxCount(List<StoreResFormatFilter.NoConvertAuxCount> noConvertAuxCount) {
            this.noConvertAuxCount = noConvertAuxCount;
        }

        public boolean sameItem(StoreInItem storeInItem) {
            //TODO batch
            //TODO storeaArea
            return (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)
                   || floatConvertRate.equals(storeInItem.getFloatConvertRate())) &&
                    res.getId().equals(storeInItem.getRes().getId()) &&
                    StoreChangeHelper.sameFormat(storeInItem.getFormats(), formats);
        }
    }

}
