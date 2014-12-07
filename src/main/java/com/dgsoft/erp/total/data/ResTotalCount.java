package com.dgsoft.erp.total.data;

import com.dgsoft.common.DataFormat;
import com.dgsoft.common.ExportRender;
import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.total.ResFormatGroupStrategy;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 11/10/14.
 */
public class ResTotalCount implements ResCount {

    public static ResTotalCount ZERO(Res res) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return new ResTotalCount(res, BigDecimal.ZERO, BigDecimal.ZERO);
        } else {
            return new ResTotalCount(res, BigDecimal.ZERO);
        }
    }

    public static final ResTotalCount ZERO = new ResTotalCount(BigDecimal.ZERO, BigDecimal.ZERO);

    public static ResCount total(Collection<? extends StoreResCountEntity> datas) {
        ResCount result = ZERO;
        for (StoreResCountEntity data : datas) {
            result = result.add(new StoreResCount(data.getStoreRes(), data.getMasterCount()));
        }
        return result;
    }

    private Res res;

    private BigDecimal masterCount;

    private BigDecimal auxCount;

    private ResTotalCount(BigDecimal masterCount, BigDecimal auxCount) {
        this.masterCount = masterCount;
        this.auxCount = auxCount;
    }

    public ResTotalCount(ResCount storeResCount) {
        this.res = storeResCount.getRes();
        this.masterCount = storeResCount.getMasterCount();
        this.auxCount = storeResCount.getAuxCount();
    }

    public ResTotalCount(Res res, BigDecimal masterCount) {
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("float Convert unit must set auxCount");
        }
        this.res = res;
        this.masterCount = masterCount;
    }

    public ResTotalCount(Res res, BigDecimal masterCount, BigDecimal auxCount) {
        if (!res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            throw new IllegalArgumentException("only float Convert unit can set auxCount");
        }
        this.res = res;
        this.masterCount = masterCount;
        this.auxCount = auxCount;
    }

    @Override
    public Res getRes() {
        return res;
    }

    @Override
    public ResCount add(ResCount other) {
        if (res == null)
            return new ResTotalCount(other);
        if(other.getRes() == null){
            return new ResTotalCount(this);
        }

        if (!getRes().equals(other.getRes())) {
            throw new IllegalArgumentException("only same res can do this oper");
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return new ResTotalCount(res, masterCount.add(other.getMasterCount()), auxCount.add(other.getAuxCount()));
        } else {
            return new ResTotalCount(res, masterCount.add(other.getMasterCount()));
        }
    }

    @Override
    public ResCount subtract(ResCount other) {
        if(res == null){
            if (other.getRes() == null){
                return ZERO;
            }else{
                if (UnitGroup.UnitGroupType.FLOAT_CONVERT.equals(other.getRes())){
                    return new ResTotalCount(other.getRes(),BigDecimal.ZERO.subtract(other.getMasterCount()),BigDecimal.ZERO.subtract(other.getAuxCount()));
                }else{
                    return new ResTotalCount(other.getRes(),BigDecimal.ZERO.subtract(other.getMasterCount()));
                }
            }
        }
        if(other.getRes() == null){
            return new ResTotalCount(this);
        }
        if (!getRes().equals(other.getRes())) {
            throw new IllegalArgumentException("only same res can do this oper");
        }
        if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
            return new ResTotalCount(res, masterCount.subtract(other.getMasterCount()), auxCount.subtract(other.getAuxCount()));
        } else {
            return new ResTotalCount(res, masterCount.subtract(other.getMasterCount()));
        }
    }

    public BigDecimal getMasterCount() {
        return masterCount;
    }

    public BigDecimal getAuxCount() {
        return auxCount;
    }

    @Override
    public BigDecimal getCountByUnit(ResUnit resUnit) {
        if (res == null){
            return BigDecimal.ZERO;
        }
        if (resUnit.isMasterUnit()) {
            return getMasterCount();
        } else {
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                if (resUnit.getId().equals(res.getUnitGroup().getFloatAuxiliaryUnit().getId())) {
                    return getAuxCount();
                }
                throw new IllegalArgumentException("invlid unit");
            } else if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FIX_CONVERT)) {
                return DataFormat.format(getMasterCount().divide(resUnit.getConversionRate(), 10, BigDecimal.ROUND_HALF_UP), resUnit.getCountFormate());


            } else {
                throw new IllegalArgumentException("invlid unit");
            }
        }
    }

    @Deprecated
    public BigDecimal getMasterUnitCount() {
        return getMasterCount();
    }

    @Deprecated
    public BigDecimal getAuxUnitCount() {
        if (auxCount == null) {
            return BigDecimal.ZERO;
        }
        return getAuxCount();
    }

    @Override
    public int compareTo(ResCount o) {
        return getMasterCount().compareTo(o.getMasterCount());
    }


    public static class FormatCountGroupStrategy<E extends StoreResCountEntity> extends ResFormatGroupStrategy<E, ResCount> {
        @Override
        public ResCount totalGroupData(Collection<E> datas) {
            return ResTotalCount.total(datas);
        }
    }


    public static class ResCountGroupStrategy<E extends StoreResCountEntity> implements TotalGroupStrategy<Res, E, ResCount> {

        @Override
        public Res getKey(E e) {
            return e.getRes();
        }

        @Override
        public ResCount totalGroupData(Collection<E> datas) {
            return ResTotalCount.total(datas);
        }
    }

    public static class StoreResCountGroupStrategy<E extends StoreResCountEntity> implements TotalGroupStrategy<StoreRes, E, ResCount> {


        @Override
        public StoreRes getKey(E e) {
            return e.getStoreRes();
        }

        @Override
        public ResCount totalGroupData(Collection<E> datas) {
            return ResTotalCount.total(datas);
        }
    }


    public static abstract class CountExportStrategy<E,T extends TotalDataGroup.GroupTotalData> implements TotalDataGroup.TotalDataExportStrategy<E,T>{

        protected int outCount(int row, int col, ResCount count, ExportRender render) {
            if (count == null) {
                col = col + 4;
                return col;
            }
            render.cell(row, col++, count.getMasterCount().doubleValue());
            render.cell(row, col++, count.getRes().getUnitGroup().getMasterUnit().getName());

            if (count.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))
                render.cell(row, col, count.getAuxCount().doubleValue());
            col++;

            if (count.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT))
                render.cell(row, col, count.getRes().getUnitGroup().getFloatAuxiliaryUnit().getName());
            col++;
            return col;
        }
    }


}
