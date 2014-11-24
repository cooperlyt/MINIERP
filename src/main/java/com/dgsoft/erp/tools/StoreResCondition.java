package com.dgsoft.erp.tools;

import com.dgsoft.erp.action.ResCategoryHome;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.action.ResHome;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.log.Logging;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14-4-28
 * Time: 下午3:06
 */
@Name("storeResCondition")
@Scope(ScopeType.CONVERSATION)
public class StoreResCondition implements Serializable {

    @In(create = true)
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

    public void resCategorySelected() {
        storeResEntity = null;
        resHome.clearInstance();
    }

    public void resSelected() {
        Logging.getLog(StoreResCondition.class).debug("resSelect id is:" + resHome.getId());
        if (resHome.isIdDefined()) {
            storeResEntity = new StoreResEntity(resHome.getInstance());
            storeResEntity.setFloatConvertRate(null);
        } else {
            storeResEntity = null;
        }
    }

    public List<String> getSearchResCategoryIds() {
        if (isCodeSearch()) {
            return null;
        }
        if ((storeResEntity == null) && (resCategoryHome != null) && resCategoryHome.isIdDefined()) {
            return resCategoryHome.getIdAndChildIds();
        } else {
            return null;
        }
    }

    public String getSearchResCatesoryIdsStr() {
        List<String> ids = getSearchResCategoryIds();
        if (ids == null) {
            return null;
        } else {
            return ids.toString();
        }
    }

    private boolean isCodeSearch() {
        return (resCode != null) && !"".equals(resCode.trim());
    }

    // isStoreResSearch() ==  !isResSearch()
    public boolean isResSearch() {
        if (storeResEntity == null) {
            return true;
        }
        if (UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(storeResEntity.getRes().getUnitGroup().getType()) &&
                (storeResEntity.getFloatConvertRate() != null))
            return false;
        for (Format format : storeResEntity.getFormats()) {
            if (format.getFormatValue() != null) {
                return false;
            }
        }
        return true;
    }

    public boolean isFullStoreResSearch() {
        if (storeResEntity == null) {
            return false;
        }
        if (UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(storeResEntity.getRes().getUnitGroup().getType()) &&
                (storeResEntity.getFloatConvertRate() == null)) {
            return false;
        }

        for (Format format : storeResEntity.getFormats()) {
            if (format.getFormatValue() == null) {
                return false;
            }
        }
        return true;
    }


    public String getSearchResId() {
        if (isCodeSearch()) {
            return null;
        }
        if (isResSearch() && (storeResEntity != null)) {
            return storeResEntity.getRes().getId();
        } else {
            return null;
        }
    }

    public BigDecimal getSearchFloatConvertRate() {
        if (isCodeSearch()) {
            return null;
        }
        if (isResSearch() && (storeResEntity != null) &&
                storeResEntity.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return storeResEntity.getFloatConvertRate();
        } else {
            return null;
        }
    }

    public String getMatchStoreResIdsStr() {
        List<String> ids = getMatchStoreResIds();
        if (ids == null) {
            return null;
        } else {
            return ids.toString();
        }
    }

    public List<String> getMatchStoreResIds() {
        if (isCodeSearch()) {
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


    public boolean isMatchStoreRes(StoreRes storeRes) {
        if (!isResSearch()) {
            return (resHelper.matchFormat(storeResEntity.getFormats(), storeRes) &&
                    (!storeResEntity.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                            (storeResEntity.getFloatConvertRate() == null) ||
                            (storeResEntity.getFloatConvertRate().compareTo(storeRes.getFloatConversionRate()) == 0)));
        } else if (storeResEntity != null) {
            return storeRes.getRes().equals(storeResEntity.getRes());
        } else if (resCategoryHome.isIdDefined()) {
            return getSearchResCategoryIds().contains(storeRes.getRes().getResCategory().getId());
        } else {
            return true;
        }
    }


    public List<StoreRes> getMatchStoreReses() {

        List<StoreRes> result = new ArrayList<StoreRes>();

        if (!isResSearch() && (storeResEntity != null)) {

            for (StoreRes storeRes : resHome.getInstance().getStoreReses()) {

                if (resHelper.matchFormat(storeResEntity.getFormats(), storeRes) &&
                        (!storeResEntity.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT) ||
                                (storeResEntity.getFloatConvertRate() == null) ||
                                (storeResEntity.getFloatConvertRate().compareTo(storeRes.getFloatConversionRate()) == 0))) {
                    result.add(storeRes);

                }
            }

        }
        return result;

    }

    public StoreRes createStoreResByCondition() {
        if (!isFullStoreResSearch()) {
            throw new IllegalArgumentException("must full confition");
        }

        StoreRes result = new StoreRes();
        result.setEnable(true);
        result.setRes(storeResEntity.getRes());
        if (UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(
                storeResEntity.getRes().getUnitGroup().getType()))
            result.setFloatConversionRate(storeResEntity.getFloatConvertRate());

        for (Format format : storeResEntity.getFormats()) {
            result.getFormats().add(new Format(result, format.getFormatDefine(), format.getFormatValue()));
        }
        result.setCode(resHelper.genStoreResCode(result));
        return result;
    }

    public String getStoreResCode() {
        return resCode;
    }

    public void setStoreResCode(String resCode) {
        this.resCode = resCode;
    }

    public void reset() {
        storeResEntity = null;
        resCategoryHome.clearInstance();
        resCode = null;
    }

    public boolean isResDefined() {
        return storeResEntity != null;
    }
}
