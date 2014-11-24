package com.dgsoft.erp.total.data;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;

import java.math.BigDecimal;

/**
 * Created by cooper on 11/13/14.
 */
public interface ResCount extends TotalDataGroup.GroupTotalData, Comparable<ResCount>{

    public Res getRes();

    public ResCount add(ResCount other);

    public ResCount subtract(ResCount other);

    public BigDecimal getMasterCount();

    public BigDecimal getAuxCount();

    public BigDecimal getCountByUnit(ResUnit resUnit);

}
