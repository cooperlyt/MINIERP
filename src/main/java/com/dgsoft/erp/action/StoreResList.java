package com.dgsoft.erp.action;

import com.dgsoft.erp.ErpEntityQuery;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import org.jboss.seam.annotations.Name;

import java.math.BigDecimal;

/**
 * Created by cooper on 2/17/14.
 */
@Name("storeResList")
public class StoreResList extends ErpEntityQuery<StoreRes>{

    private BigDecimal floatConvertRate;

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
    }
}
