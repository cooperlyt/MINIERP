package com.dgsoft.erp.model.api;

import com.dgsoft.erp.model.StoreRes;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: cooperlee
 * Date: 10/03/14
 * Time: 14:27
 */
public class StoreResPriceGroup extends ArrayList<StoreResPrice> {

    public StoreResPriceGroup() {
        super();
    }

    public StoreResPriceGroup(Collection<? extends StoreResPriceEntity> items) {
        super();
        this.putAll(items);
    }

    public void putAll(Collection<? extends StoreResPriceEntity> c) {

        for (StoreResPriceEntity re : c) {
            put(re);
        }

    }

    @Override
    public boolean addAll(int integer, Collection<? extends StoreResPrice> c) {

        throw new IllegalArgumentException("cant call this function");
    }

    @Override
    public boolean addAll(Collection<? extends StoreResPrice> c) {
        throw new IllegalArgumentException("cant call this function");
    }

    @Override
    public boolean add(StoreResPrice p) {
        throw new IllegalArgumentException("cant call this function");
    }

    @Override
    public void add(int integer, StoreResPrice p) {
        throw new IllegalArgumentException("cant call this function");
    }

    public <E extends StoreResPriceEntity> boolean put(E e) {
        for (StoreResPrice re : this) {
            if (re.isSameItem(e)) {
                re.add(e);
                return true;
            }
        }
        return super.add(new StoreResPrice(e.getMoney(), e.getResUnit(), e.getRebate(), e.getCount(), e.getStoreRes(),e.isPresentation()));
    }

    public BigDecimal getTotalPrice() {
        BigDecimal result = BigDecimal.ZERO;
        for (StoreResPrice re : this) {
            result = result.add(re.getTotalPrice());
        }
        return result;
    }

}
