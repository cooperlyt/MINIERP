package com.dgsoft.common;

import java.util.*;

/**
 * Created by cooper on 3/13/14.
 */
public class TotalDataGroup<K, V> extends HashMap<K, List<V>> {

    private TotalDataGroup() {
        super();
    }

    public static <K, V> TotalDataGroup<K, V> groupBy(Collection<V> values, TotalGroupStrategy<K, V> groupStrategy) {
        TotalDataGroup<K, V> result = new TotalDataGroup<K, V>();
        for (V value : values) {
            K valueKey = groupStrategy.getKey(value);

            List<V> valueList = result.get(valueKey);
            if (valueList == null) {
                valueList = new ArrayList<V>();
                result.put(valueKey, valueList);
            }
            valueList.add(value);
        }

        return result;
    }


    public static <K, V, T> TotalDataGroup<T, TotalDataGroup<K, V>> groupBy(TotalDataGroup<T, V> values,
                                                                            TotalGroupStrategy<K, V> groupStrategy) {
        TotalDataGroup<T, TotalDataGroup<K, V>> result = new TotalDataGroup<T, TotalDataGroup<K, V>>();
        for (Map.Entry<T,List<V>> entry: values.entrySet()){

            TotalDataGroup<K, V> subGroup = groupBy(entry.getValue(), groupStrategy);
            result.put(entry.getKey(), );
        }
    }


    public static <V extends Comparable<? super V>> void sort(TotalDataGroup<?, V> group) {
        for (List<V> value : group.values()) {
            Collections.sort(value);
        }
    }

    public static <V> void sort(TotalDataGroup<?, V> group, Comparator<? super V> c) {
        for (List<V> value : group.values()) {
            Collections.sort(value, c);
        }
    }


    public List<V> allValues() {
        List<V> result = new ArrayList<V>();
        for (List<V> value : values()) {
            result.addAll(value);
        }
        return result;
    }


}
