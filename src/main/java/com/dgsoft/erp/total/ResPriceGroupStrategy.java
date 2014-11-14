package com.dgsoft.erp.total;

import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.ResUnit;
import com.dgsoft.erp.model.api.StoreResPriceEntity;

/**
 * Created by cooper on 11/14/14.
 */
public abstract class ResPriceGroupStrategy<E extends StoreResPriceEntity,T> implements TotalGroupStrategy<ResPriceGroupStrategy.PriceItemKey,E,T> {


    @Override
    public PriceItemKey getKey(E e) {
        return new PriceItemKey(e.getRes(),e.getResUnit());
    }


    public static class PriceItemKey implements Comparable<ResPriceGroupStrategy.PriceItemKey>{

        private Res res;

        private ResUnit resUnit;

        public PriceItemKey(Res res, ResUnit resUnit) {
            this.res = res;
            this.resUnit = resUnit;
        }

        public Res getRes() {
            return res;
        }

        public ResUnit getResUnit() {
            return resUnit;
        }

        @Override
        public int compareTo(PriceItemKey o) {
           int result = getRes().compareTo(o.getRes());
            if (result == 0){
                return getResUnit().getId().compareTo(o.getResUnit().getId());
            }
            return result;
        }

        public boolean equals(Object other) {
            if ((this == other))
                return true;
            if ((other == null))
                return false;
            if (!(other instanceof PriceItemKey))
                return false;
            PriceItemKey castOther = (PriceItemKey) other;

            return (getRes().equals(castOther.getRes()) && getResUnit().equals(castOther.getResUnit()));
        }

        public int hashCode() {
            int result = 17;

            result = 37 * result
                    + (getRes() == null ? 0 : this.getRes().hashCode());
            result = 37 * result
                    + (getResUnit() == null ? 0 : this.getResUnit().hashCode());
            return result;
        }

    }

}
