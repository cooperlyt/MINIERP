package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 5/23/14.
 */
public class AreaResSaleGroupData {

    private Object areaId;

    private String resId;

    private String resName;

    private BigDecimal money;

    private BigDecimal count;

    public AreaResSaleGroupData() {
    }

    public AreaResSaleGroupData(Object areaId, String resId, String resName, BigDecimal money, BigDecimal count) {
        this.areaId = areaId;
        this.resId = resId;
        this.resName = resName;
        this.money = money;
        this.count = count;
    }

    public Object getAreaId() {
        return areaId;
    }

    public String getResId() {
        return resId;
    }

    public String getResName() {
        return resName;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public BigDecimal getCount() {
        return count;
    }
}
