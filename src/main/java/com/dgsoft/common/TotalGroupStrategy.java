package com.dgsoft.common;

import java.util.Collection;

/**
 * Created by cooper on 3/13/14.
 */
public interface TotalGroupStrategy<K extends TotalDataGroup.GroupKey, V, T extends TotalDataGroup.GroupTotalData> {


    public abstract K getKey(V v);

    public abstract T totalGroupData(Collection<V> datas);

}
