package com.dgsoft.erp.tools;


import com.dgsoft.common.helper.CollectionTools;
import org.jboss.seam.annotations.Name;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;

/**
 * Created by cooper on 6/22/14.
 */
@Name("storeResFilter")
public class StoreResFilter implements Serializable{

    private List<String> resCategoryIds;

    private List<String> storeResIds;

    private BigDecimal floatConvertRate;

    private String resId;

    public void setResCategoryIds(List<String> resCategoryIds) {
        this.resCategoryIds = resCategoryIds;
    }

    public void setResCategoryIdsStr(String str){
        resCategoryIds = CollectionTools.instance().strToList(str);
    }

    public void setStoreResIds(List<String> storeResIds) {
        this.storeResIds = storeResIds;
    }

    public void setStoreResIdsStr(String str){
        storeResIds = CollectionTools.instance().strToList(str);
    }

    public void setResId(String resId) {
        this.resId = resId;
    }

    public void setFloatConvertRate(BigDecimal floatConvertRate) {
        this.floatConvertRate = floatConvertRate;
    }

    public List<String> getResCategoryIds() {
        return resCategoryIds;
    }

    public List<String> getStoreResIds() {
        return storeResIds;
    }

    public BigDecimal getFloatConvertRate() {
        return floatConvertRate;
    }

    public String getResId() {
        return resId;
    }
}
