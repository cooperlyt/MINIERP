package com.dgsoft.common;

/**
 * Created by cooper on 3/15/14.
 */
public interface TotalDataUnionStrategy<K,V> {

    public abstract K getKey(V v);

    public abstract V unionData(V v1, V v2);

}
