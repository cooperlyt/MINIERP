package com.dgsoft.erp.total;

import com.dgsoft.common.TotalDataGroup;
import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCountEntity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 3/16/14.
 */
public class StoreResGroupStrategy<E extends StoreResCountEntity> implements TotalGroupStrategy<Res,E> {

    @Override
    public Res getKey(E e) {
        return e.getRes();
    }

    @Override
    public Object totalGroupData(Collection<E> datas) {
        ResTotalData result = new ResTotalData();
        for (StoreResCountEntity data: datas){
            result.add(data);
        }
        return result;
    }


    public class ResTotalData {

        private BigDecimal masterUnitCount;

        private BigDecimal auxUnitCount;

        public ResTotalData() {
            masterUnitCount = BigDecimal.ZERO;
            auxUnitCount = BigDecimal.ZERO;
        }

        public void add(StoreResCountEntity data) {
            masterUnitCount = masterUnitCount.add(data.getMasterCount());
            if (data.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                auxUnitCount = auxUnitCount.add(data.getAuxCount());
            }
        }

        public BigDecimal getMasterUnitCount() {
            return masterUnitCount;
        }

        public BigDecimal getAuxUnitCount() {
            return auxUnitCount;
        }
    }
}
