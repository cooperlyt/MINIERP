package com.dgsoft.erp.tools;

import com.dgsoft.erp.action.ResCategoryHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.action.StoreResHome;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.ResCategory;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Logging;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-28
 * Time: 下午3:06
 */
@Name("storeResCondition")
@Scope(ScopeType.CONVERSATION)
public class StoreResCondition {

    @In(create=true)
    private ResHome resHome;

    @In
    private ResHelper resHelper;

    @In(required = false)
    private ResCategoryHome resCategoryHome;

    private StoreResEntity storeResEntity;

    private String resCode = null;

    public StoreResEntity getStoreResEntity() {
        return storeResEntity;
    }

    public void setStoreResEntity(StoreResEntity storeResEntity) {
        this.storeResEntity = storeResEntity;
    }

    public void resSelected() {
        Logging.getLog(StoreResCondition.class).debug("resSelect id is:" + resHome.getId());
        if (resHome.isIdDefined()) {
            storeResEntity = new StoreResEntity(resHome.getInstance());
        } else {
            storeResEntity = null;
        }
    }

    public List<String> getSearchResCategoryIds(){
        if (isCodeSearch()){
            return null;
        }
        if ((storeResEntity == null) && (resCategoryHome != null) && resCategoryHome.isIdDefined()){
            return resCategoryHome.getIdAndChildIds();
        }else {
            return null;
        }
    }

    private boolean isCodeSearch(){
        return (resCode != null) && !"".equals(resCode.trim());
    }

    public boolean isResSearch() {
        if (storeResEntity == null) {
            return true;
        }
        for (Format format : storeResEntity.getFormats()) {
            if (format.getFormatValue() != null) {
                return false;
            }
        }
        return true;
    }

    public String getSearchResId() {
        if (isCodeSearch()){
            return null;
        }
        if (isResSearch() && (storeResEntity != null)) {
            return storeResEntity.getRes().getId();
        } else {
            return null;
        }
    }

    public BigDecimal getSearchFloatConvertRate(){
        if (isCodeSearch()){
            return null;
        }
        if (isResSearch() && (storeResEntity != null) &&
                storeResEntity.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            return storeResEntity.getFloatConvertRate();
        }else{
            return null;
        }
    }

    public List<String> getMatchStoreResIds() {
        if (isCodeSearch()){
            return new ArrayList<String>(0);
        }
        List<String> result = new ArrayList<String>();

        if (!isResSearch() && (storeResEntity != null)) {

            for (StoreRes storeRes : resHome.getInstance().getStoreReses()) {

                if (resHelper.matchFormat(storeResEntity.getFormats(), storeRes) &&
                        (!storeResEntity.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                                (storeResEntity.getFloatConvertRate() == null) ||
                                (storeResEntity.getFloatConvertRate().compareTo(storeRes.getFloatConversionRate()) == 0))) {
                    result.add(storeRes.getId());

                }
            }
            if (result.isEmpty()) {
                result.add("-1");
            }
        }
        return result;
    }

    public String getStoreResCode() {
        return resCode;
    }

    public void setStoreResCode(String resCode) {
        this.resCode = resCode;
    }

    public void reset(){
        storeResEntity = null;
        resCategoryHome.clearInstance();
        resCode = null;
    }
}
