package com.dgsoft.erp.total;

import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.OrderItem;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.UnitGroup;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResPriceEntity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created by cooper on 3/16/14.
 */
public class StoreResGroupStrategy<E extends StoreResCountEntity> implements TotalGroupStrategy<Res, E> {

    @Override
    public Res getKey(E e) {
        return e.getRes();
    }

    @Override
    public Object totalGroupData(Collection<E> datas) {
        ResTotalData result = new ResTotalData();
        for (StoreResCountEntity data : datas) {
            result.add(data);
        }
        return result;
    }


    public static class ResTotalData {

        private BigDecimal masterUnitCount;

        private BigDecimal auxUnitCount;

        private BigDecimal needAddCount;

        //private BigDecimal needMoney;

        private BigDecimal money;

        public ResTotalData() {
            masterUnitCount = BigDecimal.ZERO;
            auxUnitCount = BigDecimal.ZERO;
            needAddCount = BigDecimal.ZERO;
            //needMoney = BigDecimal.ZERO;
            money = BigDecimal.ZERO;
        }

        public void add(StoreResCountEntity data) {
            masterUnitCount = masterUnitCount.add(data.getMasterCount());
            if (data.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                auxUnitCount = auxUnitCount.add(data.getAuxCount());
            }
            if ((data instanceof OrderItem) && data.getRes().getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){

                needAddCount = needAddCount.add(((OrderItem) data).getNeedAddCount());
                //needMoney = needMoney.add(((OrderItem) data).getNeedMoney());
            }
            if (data instanceof StoreResPriceEntity){
                money = money.add(((StoreResPriceEntity)data).getTotalMoney());
            }
        }

        public BigDecimal getMasterUnitCount() {
            return masterUnitCount;
        }

        public BigDecimal getAuxUnitCount() {
            return auxUnitCount;
        }

        public BigDecimal getMoney() {
            return money;
        }

        public BigDecimal getNeedAddCount() {
            return needAddCount;
        }

        @Deprecated
        public BigDecimal getNeedMoney() {
            return BigDecimal.ZERO;
        }

        @Deprecated
        public BigDecimal getNeedAddMoney() {
            return BigDecimal.ZERO;
        }
    }
}
