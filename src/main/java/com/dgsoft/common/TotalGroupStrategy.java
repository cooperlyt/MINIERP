package com.dgsoft.common;

/**
 * Created by cooper on 3/13/14.
 */
public interface TotalGroupStrategy<K extends Comparable<? super K>,V> {


    public abstract K getKey(V v);

}
