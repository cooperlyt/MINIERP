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
public class StoreResCountGroup<E extends StoreResCountEntity> extends HashMap<StoreRes, E> implements java.io.Serializable {

    public StoreResCountGroup(Collection<E> values) {
        super();
        putAll(values);
    }

    public StoreResCountGroup() {
        super();
    }

    public List<E> getStoreResCountList() {

        return new ArrayList<E>(values());
    }


    public E put(E v) {

        resGroupList = null;
        E result = get(v.getStoreRes());
        if (result == null) {
            return super.put(v.getStoreRes(), v);
        } else {
            result.add(v);
            return result;
        }
    }


    public void putAll(Collection<E> values) {
        for (E v : values) {
            put(v);
        }

    }

    @Override
    public E put(StoreRes k, E v) {
        throw new IllegalArgumentException("cant't this function");
    }

    public Map<Res, List<E>> getResGroupMap() {
        Map<Res, List<E>> result = new HashMap<Res, List<E>>();
        for (E v : values()) {
            List<E> rv = result.get(v.getStoreRes().getRes());
            if (rv == null) {
                rv = new ArrayList<E>();
                result.put(v.getStoreRes().getRes(), rv);
            }
            rv.add(v);
        }

        return result;
    }

    private List<ResCountTotal<StoreResCount>> resGroupList = null;

    public List<ResCountTotal<StoreResCount>> getResGroupList() {
        initResGroupList();
        return resGroupList;
    }

    public void initResGroupList() {
        if (resGroupList == null) {
            resGroupList = new ArrayList<ResCountTotal<StoreResCount>>();
            for (Map.Entry<Res, List<E>> entry : getResGroupMap().entrySet()) {
                resGroupList.add(new ResCountTotal(entry.getKey(), entry.getValue()));
            }
            Collections.sort(resGroupList, new Comparator<ResCountTotal<StoreResCount>>() {
                @Override
                public int compare(ResCountTotal<StoreResCount> o1, ResCountTotal<StoreResCount> o2) {
                    return o1.getRes().getId().compareTo(o2.getRes().getId());
                }
            });
        }
    }


    public static class ResCountTotal<E extends StoreResCountEntity> {

        private Res res;

        private List<E> values;

        public ResCountTotal(Res res, List<E> values) {
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
            for (E e : values) {
                result = result.add(e.getMasterCount());
            }
            return DataFormat.format(result, res.getUnitGroup().getMasterUnit().getCountFormate());
        }

        public BigDecimal getTotalAuxCount() {
            if (res.getUnitGroup().getType().equals(UnitGroup.UnitGroupType.FLOAT_CONVERT)) {
                BigDecimal result = BigDecimal.ZERO;
                for (E e : values) {
                    result = result.add(e.getAuxCount());
                }
                return DataFormat.format(result, res.getUnitGroup().getFloatAuxiliaryUnit().getCountFormate());
            } else {
                return null;
            }
        }

    }

}
