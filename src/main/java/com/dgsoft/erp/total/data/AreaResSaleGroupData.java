package com.dgsoft.erp.total.data;

import java.math.BigDecimal;

/**
 * Created by cooper on 5/23/14.
 */
public class AreaResSaleGroupData {

    private String areaId;

    private String areaName;

    private String resId;

    private String resName;

    private BigDecimal money;

    private BigDecimal count;

    public AreaResSaleGroupData() {
    }

    public AreaResSaleGroupData(String areaId,String areaName, String resId, String resName, BigDecimal money, BigDecimal count) {
        this.areaId = areaId;
        this.resId = resId;
        this.resName = resName;
        this.money = money;
        this.count = count;
        this.areaName = areaName;
    }

    public String getAreaId() {
        return areaId;
    }

    public String getAreaName() {
        return areaName;
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
