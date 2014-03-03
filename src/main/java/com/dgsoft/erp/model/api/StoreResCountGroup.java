package com.dgsoft.erp.model.api;

import com.dgsoft.common.DataFormat;
import com.dgsoft.erp.model.Res;
import com.dgsoft.erp.model.StoreRes;
import com.dgsoft.erp.model.UnitGroup;

import java.math.BigDecimal;
import java.util.*;

/**
 * Created by cooper on 3/3/14.
 */
public class StoreResCountGroup<V extends StoreResCountEntity> extends HashMap<StoreRes, V> {

    public <E extends V> StoreResCountGroup(Collection<E> values) {
        super();
        putAll(values);
    }

    public StoreResCountGroup() {
        super();
    }

    public List<V> getStoreResCountList() {

        return new ArrayList<V>(values());
    }

    public V put(V v) {

        V result = get(v.getStoreRes());
        if (result == null) {
            return super.put(v.getStoreRes(), v);
        } else {
            result.add(v);
            return result;
        }
    }

    public <E extends V> void putAll(Collection<E> values){
        for (V v: values){
            put(v);
        }
    }

    @Override
    public V put(StoreRes k, V v) {
        throw new IllegalArgumentException("cant't this function");
    }

    public Map<Res, List<V>> getResGroupMap() {
        Map<Res, List<V>> result = new HashMap<Res, List<V>>();
        for (V v : values()) {
            List<V> rv = result.get(v.getStoreRes().getRes());
            if (rv == null) {
                rv = new ArrayList<V>();
                result.put(v.getStoreRes().getRes(),rv);
            }
            rv.add(v);
        }

        return result;
    }

    public List<ResCountTotal<V>> getResGroupList() {
        List<ResCountTotal<V>> result = new ArrayList<ResCountTotal<V>>();
        for (Map.Entry<Res,List<V>> entry: getResGroupMap().entrySet()){
            result.add(new ResCountTotal(entry.getKey(),entry.getValue()));
        }
        Collections.sort(result,new Comparator<ResCountTotal<V>>() {
            @Override
            public int compare(ResCountTotal<V> o1, ResCountTotal<V> o2) {
                return o1.getRes().getId().compareTo(o2.getRes().getId());
            }
        });
        return result;
    }


    public static class ResCountTotal<E extends StoreResCountEntity>{

        private Res res;

        private List<E> values;

        public ResCountTotal(Res res, List<E> values){
            this.res = res;
            this.values = values;
        }

        public Res getRes() {
            return res;
        }

        public List<E> getValues() {
            return values;
        }

        public BigDecimal getTotalMasterCount() {
            BigDecimal result = BigDecimal.ZERO;
            for (E e: values){
                result = result.add(e.getMasterCount());
            }
            return DataFormat.format(result,res.getUnitGroup().getMasterUnit().getCountFormate());
        }

        public BigDecimal getTotalAuxCount(){
           if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)){
               BigDecimal result = BigDecimal.ZERO;
               for (E e: values){
                   result = result.add(e.getAuxCount());
               }
               return DataFormat.format(result,res.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
           }else{
               return null;
           }
        }

    }

}
