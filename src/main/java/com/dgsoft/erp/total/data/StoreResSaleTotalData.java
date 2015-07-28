package com.dgsoft.erp.total.data;

import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCount;

import java.math.BigDecimal;

/**
 * Created by cooper on 12/19/14.
 */
public class StoreResSaleTotalData {

    private StoreRes storeRes;

    private BigDecimal count;

    private Double avgMoney;

    private BigDecimal money;

    private BigDecimal needCount;

    private BigDecimal needMoney;


    public StoreResSaleTotalData(StoreRes storeRes, BigDecimal count,
                                 Double avgMoney, BigDecimal money, BigDecimal needCount,BigDecimal needMoney) {
        this.storeRes = storeRes;
        this.count = count;
       // this.unitId = unitId;

        this.avgMoney = avgMoney;
        this.money = money;
        this.needCount = needCount;
        this.needMoney = needMoney;
    }

    public BigDecimal getNeedMoney() {
        if (!UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(storeRes.getRes().getUnitGroup().getType()))
            return money;
        if (needMoney == null)
            return BigDecimal.ZERO;

        return needMoney;
    }

    public StoreRes getStoreRes() {
        return storeRes;
    }

    public ResCount getResCount(){
        return new StoreResCount(storeRes,count);
    }

    public Double getAvgMoney() {
        return avgMoney;
    }

    public BigDecimal getMoney() {
        return money;
    }

    public BigDecimal getNeedCount() {
        if ((needCount == null) && storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            return getResCount().getAuxCount();
        }
        if (!UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(storeRes.getRes().getUnitGroup().getType())){
            return count;
        }
        return needCount;
    }

    public void setNeedCount(BigDecimal needCount) {
        this.needCount = needCount;
    }

    public BigDecimal getAddCount(){
        if(storeRes.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
            return getNeedCount().subtract(getResCount().getAuxCount());
        }
        return BigDecimal.ZERO;
    }
}
