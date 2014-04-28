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
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.log.Logging;

import java.util.ArrayList;
import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-28
 * Time: 下午3:06
 */
@Name("storeResCondition")
public class StoreResCondition {

    @In
    private ResHome resHome;

    @In
    private ResHelper resHelper;


    private StoreResEntity storeResEntity;

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

    public boolean isResSearch() {
        if (storeResEntity == null) {
            return false;
        }
        if (storeResEntity.getRes().getUnitGroup().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))
            return false;
        for (Format format : storeResEntity.getFormats()) {
            if (format.getFormatValue() != null) {
                return false;
            }
        }
        return true;
    }

    public String getSearchResId() {
        if (isResSearch()) {
            return storeResEntity.getRes().getId();
        } else
            return null;
    }

    public List<String> getMatchStoreResIds() {
        List<String> result = new ArrayList<String>();
        if (!isResSearch() && (storeResEntity != null)) {

            for (StoreRes storeRes : resHome.getInstance().getStoreReses()) {
                if (resHelper.matchFormat(storeResEntity.getFormats(), storeRes) &&
                        (!storeResEntity.getRes().getUnitGroup().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
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


}
