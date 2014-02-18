package com.dgsoft.erp.action;

import com.dgsoft.common.utils.StringUtil;
import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.action.store.StoreResFormatFilter;
import com.dgsoft.erp.model.*;
import org.jboss.seam.Component;
import org.jboss.seam.ScopeType;
import org.jboss.seam.annotations.In;
import org.jboss.seam.annotations.Name;
import org.jboss.seam.annotations.Scope;
import org.jboss.seam.security.Credentials;

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

    @In(required = false)
    private StoreResFormatFilter storeResFormatFilter;

    @In(required = false)
    private ResHome resHome;

    private BigDecimal floatConvertRate;

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
    }

    public BigDecimal getSearchFloatConvertRate() {
        if ((resHome != null) && resHome.isIdDefined() && resHome.getInstance().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return floatConvertRate;
        } else {
            return null;
        }
    }

    public String getSearchResId() {
        if ((resHome != null) && resHome.isIdDefined()) {
            return resHome.getInstance().getId();
        } else
            return null;
    }

    public boolean isAllStoreRes() {
        return (resHome == null) || !resHome.isIdDefined();
    }

    public boolean isResSearch() {
        return (resHome != null) && resHome.isIdDefined() &&
                ((storeResFormatFilter == null) || storeResFormatFilter.isNoFormatLimit());
    }

    @Override
    public List<StoreRes> getResultList() {
        List<StoreRes> result = super.getResultList();
        if ((storeResFormatFilter != null) && !storeResFormatFilter.isNoFormatLimit()) {
            List<StoreRes> filterResult = new ArrayList<StoreRes>();


            for (StoreRes storeRes : result) {
                boolean add = true;
                Map<FormatDefine, Format> storeResFormatMap = storeRes.getFormatMap();
                for (Format format : storeResFormatFilter.getResFormatList()) {
                    if (!StringUtil.isEmpty(format.getFormatValue()) &&
                            !storeResFormatMap.get(format.getFormatDefine()).equals(format)) {
                        add = false;
                        break;

                    }

                }
                if (add){
                    filterResult.add(storeRes);
                }
            }
            result = filterResult;

        }

        return result;
    }
}
