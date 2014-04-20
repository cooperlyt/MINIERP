package com.dgsoft.erp.total;

import com.dgsoft.common.TotalGroupStrategy;
import com.dgsoft.erp.ResFormatCache;
import com.dgsoft.erp.action.ResHelper;
import com.dgsoft.erp.model.Format;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.api.StoreResCount;
import com.dgsoft.erp.model.api.StoreResCountEntity;
import com.dgsoft.erp.model.api.StoreResPriceEntity;

import java.math.BigDecimal;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 14/04/14
 * Time: 16:42
 */
public class SameFormatResGroupStrategy <E extends StoreResCountEntity> implements TotalGroupStrategy<SameFormatResGroupStrategy.StoreResFormatKey,E> {

    @Override
    public StoreResFormatKey getKey(E e) {
        return new StoreResFormatKey(e.getStoreRes());
    }

    @Override
    public Object totalGroupData(Collection<E> datas) {
        StoreResGroupStrategy.ResTotalData result = new StoreResGroupStrategy.ResTotalData();
        for (StoreResCountEntity data : datas) {
            result.add(data);
        }
        return result;
    }


    public static class StoreResFormatKey implements Comparable<StoreResFormatKey>{

        private StoreRes storeRes;

        public StoreResFormatKey(StoreRes storeRes) {
            this.storeRes = storeRes;
        }

        public StoreRes getStoreRes() {
            return storeRes;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (!(o instanceof StoreResFormatKey)) return false;

            StoreResFormatKey that = (StoreResFormatKey) o;

            if (storeRes.equals(that.getStoreRes())){
                return true;
            }

            if (ResHelper.instance().sameFormat(ResFormatCache.instance().getFormats(storeRes),ResFormatCache.instance().getFormats(that.getStoreRes()))){
                return true;
            }

            return false;
        }

        public Res getRes(){
            return storeRes.getRes();
        }

        public String getFormatTitle(){
           return ResHelper.instance().getFormatsTitle(ResFormatCache.instance().getFormats(storeRes),false);
        }

        @Override
        public int hashCode() {
            String formatId = storeRes.getRes().getId();
            for (Format format: ResFormatCache.instance().getFormats(storeRes))
                formatId = formatId + "-" + format.getFormatDefine().getId() + ":" + format.getFormatValue();
            return formatId.hashCode();
        }

        @Override
        public int compareTo(StoreResFormatKey o) {
            return storeRes.compareTo(o.getStoreRes());
        }

        @Override
        public String toString(){
            return storeRes.getRes().getName() + ResHelper.instance().getFormatsTitle(ResFormatCache.instance().getFormats(storeRes),false);
        }
    }
}
