package com.dgsoft.erp.action;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.*;
import com.dgsoft.erp.model.api.StoreResEntity;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.*;
import org.jboss.seam.log.Log;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by cooper on 2/17/14.
 */
@Name("storeResList")
@Scope(ScopeType.CONVERSATION)
public class StoreResList extends ErpEntityQuery<StoreRes> {


    private static final String EJBQL = "select storeRes from StoreRes storeRes left join fetch storeRes.res";

    private static final String[] RESTRICTIONS = {
            "storeRes.res.id = #{storeResList.searchResId}",
            "storeRes.floatConversionRate = #{storeResList.searchFloatConvertRate}"};


    public StoreResList() {
        setEjbql(EJBQL);
        setRestrictionExpressionStrings(Arrays.asList(RESTRICTIONS));
        setRestrictionLogicOperator("and");
    }

    // @In(required = false)
    //  private StoreResFormatFilter storeResFormatFilter;

    @Logger
    private Log log;

    @In
    private ResHelper resHelper;

    private StoreResEntity resCondition = null;

    public StoreResEntity getResCondition() {
        return resCondition;
    }

    public void reset(){
        resCondition = null;
        refresh();
    }

    @Observer(value = "erp.storeResLocateSelected", create = false)
    public void selectedStoreRes(StoreRes storeRes) {
        log.debug("storeResFormat selectedStoreRes Observer ");
        resCondition = new StoreResEntity(storeRes, resHelper.
                getFormatHistory(storeRes.getRes()),
                resHelper.getFloatConvertRateHistory(storeRes.getRes()));
    }


    @Observer(value = "erp.resLocateSelected", create = false)
    public void selectedRes(Res res) {
        resCondition = new StoreResEntity(res, resHelper.
                getFormatHistory(res),
                resHelper.getFloatConvertRateHistory(res));
    }


    public BigDecimal getSearchFloatConvertRate() {
        if ((resCondition != null) && resCondition.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return resCondition.getFloatConvertRate();
        } else {
            return null;
        }
    }

    public String getSearchResId() {
        if (resCondition != null) {
            return resCondition.getRes().getId();
        } else
            return null;
    }

    public boolean isAllStoreRes() {
        return resCondition == null;
    }

    public boolean isResSearch() {
        return (resCondition != null) && resCondition.isNoFormatTyped();
    }

    @Override
    public List<StoreRes> getResultList() {
        List<StoreRes> result = super.getResultList();
        if ((resCondition != null) && !resCondition.isNoFormatTyped()) {
            List<StoreRes> filterResult = new ArrayList<StoreRes>();


            for (StoreRes storeRes : result) {
                boolean add = true;
                Map<FormatDefine, Format> storeResFormatMap = storeRes.getFormatMap();
                for (Format format : resCondition.getFormats()) {
                    if (!DataFormat.isEmpty(format.getFormatValue()) &&
                            !storeResFormatMap.get(format.getFormatDefine()).equals(format)) {
                        add = false;
                        break;

                    }

                }
                if (add) {
                    filterResult.add(storeRes);
                }
            }
            result = filterResult;

        }
        log.debug("getResultList:" + result.size());
        return result;
    }

    public String getResultSearchResId() {
        if (isResSearch()) {
            return getSearchResId();
        } else {

            return null;
        }
    }

    public BigDecimal getResultSearchFloatConvertRate() {
        if (isResSearch()) {
            return getSearchFloatConvertRate();
        } else {
            return null;
        }

    }

    public List<StoreRes> getFilterResultList(){

        if (!isAllStoreRes() && !isResSearch()) {

            return getResultList();
        }else{
            return null;
        }
    }
}
