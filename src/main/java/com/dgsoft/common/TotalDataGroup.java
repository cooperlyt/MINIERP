package com.dgsoft.common;

import java.util.*;

/**
 * Created by cooper on 3/13/14.
 */
public class TotalDataGroup<K extends Comparable, V> implements Comparable<TotalDataGroup> {


    private TotalDataGroup(K key) {
        super();
        this.key = key;
    }

    private K key;

    private List<TotalDataGroup<?, V>> childGroup;

    private List<V> values = new ArrayList<V>();

    public boolean isLeaf() {
        return ((childGroup == null) || childGroup.isEmpty());
    }

    public K getKey() {
        return key;
    }


    public List<TotalDataGroup<?, V>> getChildGroup() {
        return childGroup;
    }

    public List<V> getValues() {
        return values;
    }



    //public static        Object... params

    public static <K extends Comparable<? super K>, V> List<TotalDataGroup<K, V>> groupBy(Collection<V> values,
                                                                                            TotalGroupStrategy<K, V> groupStrategy,
                                                                                            TotalGroupStrategy<?, V>... groupStrategys) {
        List<TotalDataGroup<K, V>> result = groupCollection(values, groupStrategy);



        for (TotalGroupStrategy<?, V> stragegy : groupStrategys) {
            for (TotalDataGroup<K, V> l : result) {
                groupChildBy(l, stragegy);
            }
        }
        return result;
    }

    private static <K extends Comparable<? super K>, V> List<TotalDataGroup<K, V>> groupCollection(Collection<V> values,
                                                                                                     TotalGroupStrategy<K, V> groupStrategy) {
        Map<K, TotalDataGroup<K, V>> result = new HashMap<K, TotalDataGroup<K, V>>();
        for (V value : values) {
            K valueKey = groupStrategy.getKey(value);


            TotalDataGroup<K, V> valueList = result.get(valueKey);
            if (valueList == null) {
                valueList = new TotalDataGroup<K, V>(valueKey);
                result.put(valueKey, valueList);
            }
            valueList.getValues().add(value);
        }

        List<TotalDataGroup<K, V>> listResult = new ArrayList<TotalDataGroup<K, V>>(result.values());
        Collections.sort(listResult);
        return listResult;
    }

    private static <K extends Comparable<? super K>, V> void groupChildBy(TotalDataGroup<?, V> values, TotalGroupStrategy<K, V> groupStrategy) {
        if (values.isLeaf()) {
            values.childGroup = new ArrayList<TotalDataGroup<? extends Comparable, V>>();
            for (TotalDataGroup<K, V> c : groupCollection(values.getValues(), groupStrategy)) {
                values.childGroup.add(c);
            }
        } else {
            for (TotalDataGroup<?, V> c : values.getChildGroup()) {
                groupChildBy(c, groupStrategy);
            }
        }
    }


    @Override
    public int compareTo(TotalDataGroup o) {
        return getKey().compareTo(o.getKey());
    }
}
